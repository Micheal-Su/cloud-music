package com.tacitn.gateway.config;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自定义根据端口的hash值取模访问服务提供者集群
 */
//@Configuration
public class CustomizeIpHashRule extends AbstractLoadBalancerRule {
  @Value("${server.port}")
  private Integer port;
  public Server choose(ILoadBalancer lb,Object key){
      if (lb==null){
          return null;
      }else {
          Server server = null;
          while (server==null){
              //获取可用的服务实例列表
              List<Server> upList = lb.getReachableServers();
              //获取所有的服务实例列表
              List<Server> allList = lb.getAllServers();
              int serverCount = allList.size();
              if(serverCount == 0){
                  return null;
              }
               int i=ipAddressHash(serverCount);
               server = upList.get(i);
          }
          return  server;
      }

  }

    private int ipAddressHash(int serverCount) {
        System.out.println(port.hashCode());
        int code = Math.abs(port.hashCode());
        System.out.println(code + "\n\n\n");
//        int类型 hash值和原值是一样的
        return code%serverCount;

    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }
    
    @Override
    public Server choose(Object key) {
        return choose(getLoadBalancer(),key);
    }

}
