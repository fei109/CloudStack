/**
 * 
 */
package com.cloud.network.guru;

import java.util.Map;

import javax.ejb.Local;
import javax.naming.ConfigurationException;

import org.apache.log4j.Logger;

import com.cloud.configuration.Config;
import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.deploy.DeployDestination;
import com.cloud.deploy.DeploymentPlan;
import com.cloud.exception.InsufficientAddressCapacityException;
import com.cloud.exception.InsufficientVirtualNetworkCapcityException;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.network.Network;
import com.cloud.network.NetworkVO;
import com.cloud.network.Networks.AddressFormat;
import com.cloud.network.Networks.BroadcastDomainType;
import com.cloud.network.Networks.Mode;
import com.cloud.network.Networks.TrafficType;
import com.cloud.offering.NetworkOffering;
import com.cloud.resource.Resource.ReservationStrategy;
import com.cloud.user.Account;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.component.Inject;
import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.utils.net.NetUtils;
import com.cloud.vm.NicProfile;
import com.cloud.vm.ReservationContext;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;

@Local(value={NetworkGuru.class})
public class ControlNetworkGuru extends PodBasedNetworkGuru implements NetworkGuru {
    private static final Logger s_logger = Logger.getLogger(ControlNetworkGuru.class);
    @Inject DataCenterDao _dcDao;
    String _cidr;
    String _gateway;

    @Override
    public Network design(NetworkOffering offering, DeploymentPlan plan, Network specifiedConfig, Account owner) {
        if (offering.getTrafficType() != TrafficType.Control) {
            return null;
        }
        
        NetworkVO config = new NetworkVO(offering.getTrafficType(), offering.getGuestIpType(), Mode.Static, BroadcastDomainType.LinkLocal, offering.getId(), plan.getDataCenterId());
        config.setCidr(_cidr);
        config.setGateway(_gateway);
        
        return config;
    }
    
    protected ControlNetworkGuru() {
        super();
    }
    
    @Override
    public NicProfile allocate(Network config, NicProfile nic, VirtualMachineProfile<? extends VirtualMachine> vm) throws InsufficientVirtualNetworkCapcityException,
            InsufficientAddressCapacityException {
    	
        if (config.getTrafficType() != TrafficType.Control) {
        	return null;
        }

        if(vm.getHypervisorType() == HypervisorType.VmWare && vm.getType() != VirtualMachine.Type.DomainRouter) {
        	NicProfile nicProf = new NicProfile(ReservationStrategy.Create, null, null, null, null);
            String mac = _networkMgr.getNextAvailableMacAddressInNetwork(config.getId());
            nicProf.setMacAddress(mac);
            return nicProf;
        }
        
        if (nic != null) {
            throw new CloudRuntimeException("Does not support nic specification at this time: " + nic);
        }
        
        return new NicProfile(ReservationStrategy.Start, null, null, null, null);
    }
    
    @Override
    public void deallocate(Network config, NicProfile nic, VirtualMachineProfile<? extends VirtualMachine> vm) {
    }

    @Override
    public void reserve(NicProfile nic, Network config, VirtualMachineProfile<? extends VirtualMachine> vm, DeployDestination dest, ReservationContext context) throws InsufficientVirtualNetworkCapcityException,
            InsufficientAddressCapacityException {
        assert nic.getTrafficType() == TrafficType.Control;

        if (dest.getHost().getHypervisorType() == HypervisorType.VmWare && vm.getType() == VirtualMachine.Type.DomainRouter) {
            super.reserve(nic, config, vm, dest, context);
            
            String mac = _networkMgr.getNextAvailableMacAddressInNetwork(config.getId());
            nic.setMacAddress(mac);
            return;
        }
        
        String ip = _dcDao.allocateLinkLocalIpAddress(dest.getDataCenter().getId(), dest.getPod().getId(), nic.getId(), context.getReservationId());
        nic.setIp4Address(ip);
        nic.setMacAddress(NetUtils.long2Mac(NetUtils.ip2Long(ip) | (14l << 40)));
        nic.setNetmask("255.255.0.0");
        nic.setFormat(AddressFormat.Ip4);
        nic.setGateway(NetUtils.getLinkLocalGateway());
    }

    @Override
    public boolean release(NicProfile nic, VirtualMachineProfile<? extends VirtualMachine> vm, String reservationId) {
        assert nic.getTrafficType() == TrafficType.Control;

        if (vm.getHypervisorType() == HypervisorType.VmWare && vm.getType() == VirtualMachine.Type.DomainRouter) {
        	super.release(nic, vm, reservationId);
        	return true;
        }
        
        _dcDao.releaseLinkLocalIpAddress(nic.getId(), reservationId);
        nic.setIp4Address(null);
        nic.setMacAddress(null);
        nic.setNetmask(null);
        nic.setFormat(null);
        nic.setGateway(null);
        
        return true;
    }

    @Override
    public Network implement(Network config, NetworkOffering offering, DeployDestination destination, ReservationContext context) {
        assert config.getTrafficType() == TrafficType.Control : "Why are you sending this configuration to me " + config;
        return config;
    }
    
    @Override
    public void destroy(Network config, NetworkOffering offering) {
        assert false : "Destroying a link local...Either you're out of your mind or something has changed.";
    }
    
    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        super.configure(name, params);
        
        ComponentLocator locator = ComponentLocator.getCurrentLocator();
        
        ConfigurationDao configDao = locator.getDao(ConfigurationDao.class);
        Map<String, String> dbParams = configDao.getConfiguration(params);
        
        _cidr = dbParams.get(Config.ControlCidr);
        if (_cidr == null) {
            _cidr = "169.254.0.0/16";
        }
        
        _gateway = dbParams.get(Config.ControlGateway);
        if (_gateway == null) {
            _gateway = NetUtils.getLinkLocalGateway();
        }
        
        s_logger.info("Control network setup: cidr=" + _cidr + "; gateway = " + _gateway);
        
        return true;
    }

    @Override
    public boolean trash(Network config, NetworkOffering offering, Account owner) {
        return true;
    }

}
