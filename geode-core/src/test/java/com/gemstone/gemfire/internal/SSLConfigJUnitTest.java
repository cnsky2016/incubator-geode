/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gemstone.gemfire.internal;

import com.gemstone.gemfire.distributed.internal.DistributionConfigImpl;
import com.gemstone.gemfire.test.junit.categories.IntegrationTest;
import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import static com.gemstone.gemfire.distributed.DistributedSystemConfigProperties.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test that DistributionConfigImpl handles SSL options correctly.
 * 
 */
@Category(IntegrationTest.class)
public class SSLConfigJUnitTest {

  private static final Properties SSL_PROPS_MAP     = new Properties();
  private static final Properties CLUSTER_SSL_PROPS_MAP     = new Properties();
  private static final Properties CLUSTER_SSL_PROPS_SUBSET_MAP     = new Properties();
  private static final Properties JMX_SSL_PROPS_MAP = new Properties();
  private static final Properties JMX_SSL_PROPS_SUBSET_MAP = new Properties();
  private static final Properties SERVER_SSL_PROPS_MAP = new Properties();
  private static final Properties SERVER_PROPS_SUBSET_MAP = new Properties();
  private static final Properties GATEWAY_SSL_PROPS_MAP = new Properties();
  private static final Properties GATEWAY_PROPS_SUBSET_MAP = new Properties();
  

  static {
    
    SSL_PROPS_MAP.put("javax.net.ssl.keyStoreType", "jks");
    SSL_PROPS_MAP.put("javax.net.ssl.keyStore", "/export/gemfire-configs/gemfire.keystore");
    SSL_PROPS_MAP.put("javax.net.ssl.keyStorePassword", "gemfire-key-password");
    SSL_PROPS_MAP.put("javax.net.ssl.trustStore", "/export/gemfire-configs/gemfire.truststore");
    SSL_PROPS_MAP.put("javax.net.ssl.trustStorePassword", "gemfire-trust-password");
    
    // SSL Properties for GemFire in-cluster connections
    CLUSTER_SSL_PROPS_MAP.put(CLUSTER_SSL_KEYSTORE_TYPE, "jks");
    CLUSTER_SSL_PROPS_MAP.put(CLUSTER_SSL_KEYSTORE, "/export/gemfire-configs/gemfire.keystore");
    CLUSTER_SSL_PROPS_MAP.put(CLUSTER_SSL_KEYSTORE_PASSWORD, "gemfire-key-password");
    CLUSTER_SSL_PROPS_MAP.put(CLUSTER_SSL_TRUSTSTORE, "/export/gemfire-configs/gemfire.truststore");
    CLUSTER_SSL_PROPS_MAP.put(CLUSTER_SSL_TRUSTSTORE_PASSWORD, "gemfire-trust-password");

     // Partially over-ridden SSL Properties for cluster
    CLUSTER_SSL_PROPS_SUBSET_MAP.put(CLUSTER_SSL_KEYSTORE, "/export/gemfire-configs/gemfire.keystore");
    CLUSTER_SSL_PROPS_SUBSET_MAP.put(CLUSTER_SSL_TRUSTSTORE, "/export/gemfire-configs/gemfire.truststore");
    
    // SSL Properties for GemFire JMX Manager connections
    JMX_SSL_PROPS_MAP.put(JMX_MANAGER_SSL_KEYSTORE_TYPE, "jks");
    JMX_SSL_PROPS_MAP.put(JMX_MANAGER_SSL_KEYSTORE, "/export/gemfire-configs/manager.keystore");
    JMX_SSL_PROPS_MAP.put(JMX_MANAGER_SSL_KEYSTORE_PASSWORD, "manager-key-password");
    JMX_SSL_PROPS_MAP.put(JMX_MANAGER_SSL_TRUSTSTORE, "/export/gemfire-configs/manager.truststore");
    JMX_SSL_PROPS_MAP.put(JMX_MANAGER_SSL_TRUSTSTORE_PASSWORD, "manager-trust-password");
    
    // SSL Properties for GemFire CacheServer connections
    SERVER_SSL_PROPS_MAP.put(SERVER_SSL_KEYSTORE_TYPE, "jks");
    SERVER_SSL_PROPS_MAP.put(SERVER_SSL_KEYSTORE, "/export/gemfire-configs/cacheserver.keystore");
    SERVER_SSL_PROPS_MAP.put(SERVER_SSL_KEYSTORE_PASSWORD, "cacheserver-key-password");
    SERVER_SSL_PROPS_MAP.put(SERVER_SSL_TRUSTSTORE, "/export/gemfire-configs/cacheserver.truststore");
    SERVER_SSL_PROPS_MAP.put(SERVER_SSL_TRUSTSTORE_PASSWORD, "cacheserver-trust-password");
    
   // SSL Properties for GemFire gateway connections
    GATEWAY_SSL_PROPS_MAP.put(GATEWAY_SSL_KEYSTORE_TYPE, "jks");
    GATEWAY_SSL_PROPS_MAP.put(GATEWAY_SSL_KEYSTORE, "/export/gemfire-configs/gateway.keystore");
    GATEWAY_SSL_PROPS_MAP.put(GATEWAY_SSL_KEYSTORE_PASSWORD, "gateway-key-password");
    GATEWAY_SSL_PROPS_MAP.put(GATEWAY_SSL_TRUSTSTORE, "/export/gemfire-configs/gateway.truststore");
    GATEWAY_SSL_PROPS_MAP.put(GATEWAY_SSL_TRUSTSTORE_PASSWORD, "gateway-trust-password");

    // Partially over-ridden SSL Properties for GemFire JMX Manager connections
    JMX_SSL_PROPS_SUBSET_MAP.put(JMX_MANAGER_SSL_KEYSTORE, "/export/gemfire-configs/manager.keystore");
    JMX_SSL_PROPS_SUBSET_MAP.put(JMX_MANAGER_SSL_TRUSTSTORE, "/export/gemfire-configs/manager.truststore");
    
    // Partially over-ridden SSL Properties for GemFire JMX Manager connections
    SERVER_PROPS_SUBSET_MAP.put(SERVER_SSL_KEYSTORE, "/export/gemfire-configs/cacheserver.keystore");
    SERVER_PROPS_SUBSET_MAP.put(SERVER_SSL_TRUSTSTORE, "/export/gemfire-configs/cacheserver.truststore");
    
    // Partially over-ridden SSL Properties for GemFire JMX Manager connections
    GATEWAY_PROPS_SUBSET_MAP.put(GATEWAY_SSL_KEYSTORE, "/export/gemfire-configs/gateway.keystore");
    GATEWAY_PROPS_SUBSET_MAP.put(GATEWAY_SSL_TRUSTSTORE, "/export/gemfire-configs/gateway.truststore");

  }
  
  //----- test methods ------

  @Test
  public void testMCastPortWithSSL() throws Exception {
    Properties props = new Properties( );
    // default mcast-port is not 0.
    props.setProperty(SSL_ENABLED, "true");
    
    try {
      new DistributionConfigImpl( props );
    } catch ( IllegalArgumentException e ) {
      if (! e.toString().matches( ".*Could not set \"ssl-enabled.*" ) ) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }

    props.setProperty(MCAST_PORT, "0");
    new DistributionConfigImpl( props );
  }
  
  @Test
  public void testMCastPortWithClusterSSL() throws Exception {
    Properties props = new Properties( );
    // default mcast-port is not 0.
    props.setProperty(CLUSTER_SSL_ENABLED, "true");
    
    try {
      new DistributionConfigImpl( props );
    } catch ( IllegalArgumentException e ) {
      if (! e.toString().matches( ".*Could not set \"cluster-ssl-enabled.*" ) ) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }

    props.setProperty(MCAST_PORT, "0");
    new DistributionConfigImpl( props );
  }
  
  @Test
  public void testConfigCopyWithSSL( ) throws Exception {
    boolean sslenabled = false;
    String sslprotocols = "any";
    String sslciphers = "any";
    boolean requireAuth = true;
    
    DistributionConfigImpl config = new DistributionConfigImpl( new Properties() );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );
    
    Properties props = new Properties();
    sslciphers = "RSA_WITH_GARBAGE";
    props.setProperty(SSL_CIPHERS, sslciphers);

    config = new DistributionConfigImpl( props );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );
    
    sslprotocols = "SSLv7";
    props.setProperty(SSL_PROTOCOLS, sslprotocols);

    config = new DistributionConfigImpl( props );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );

    requireAuth = false;
    props.setProperty(SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    config = new DistributionConfigImpl( props );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );

    sslenabled = true;
    props.setProperty(SSL_ENABLED, String.valueOf(sslenabled));
    props.setProperty(MCAST_PORT, "0");

    config = new DistributionConfigImpl( props );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );
    
    config = new DistributionConfigImpl( config );
    isEqual( config.getSSLEnabled(), sslenabled );
    isEqual( config.getSSLProtocols(), sslprotocols );
    isEqual( config.getSSLCiphers(), sslciphers );
    isEqual( config.getSSLRequireAuthentication(), requireAuth );
  }
  
  @Test
  public void testConfigCopyWithClusterSSL( ) throws Exception {
    boolean sslenabled = false;
    String sslprotocols = "any";
    String sslciphers = "any";
    boolean requireAuth = true;
    
    DistributionConfigImpl config = new DistributionConfigImpl( new Properties() );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );
    
    Properties props = new Properties();
    sslciphers = "RSA_WITH_GARBAGE";
    props.setProperty(CLUSTER_SSL_CIPHERS, sslciphers);

    config = new DistributionConfigImpl( props );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );
    
    sslprotocols = "SSLv7";
    props.setProperty(CLUSTER_SSL_PROTOCOLS, sslprotocols);

    config = new DistributionConfigImpl( props );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    requireAuth = false;
    props.setProperty(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    config = new DistributionConfigImpl( props );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    sslenabled = true;
    props.setProperty(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    props.setProperty(MCAST_PORT, "0");

    config = new DistributionConfigImpl( props );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );
    
    config = new DistributionConfigImpl( config );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );
  }

  @Test
  public void testManagerDefaultConfig() throws Exception {
    boolean sslenabled = false;
    String sslprotocols = "any";
    String sslciphers = "any";
    boolean requireAuth = true;

    boolean jmxManagerSsl = false;
    boolean jmxManagerSslenabled = false;
    String jmxManagerSslprotocols = "any";
    String jmxManagerSslciphers = "any";
    boolean jmxManagerSslRequireAuth = true;

    DistributionConfigImpl config = new DistributionConfigImpl( new Properties() );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );
    
    isEqual( config.getJmxManagerSSLEnabled(), jmxManagerSsl);
    isEqual( config.getJmxManagerSSLEnabled(), jmxManagerSslenabled );
    isEqual( config.getJmxManagerSSLProtocols(), jmxManagerSslprotocols );
    isEqual( config.getJmxManagerSSLCiphers(), jmxManagerSslciphers );
    isEqual( config.getJmxManagerSSLRequireAuthentication(), jmxManagerSslRequireAuth );
  }
  
  @Test
  public void testCacheServerDefaultConfig() throws Exception {
    boolean sslenabled = false;
    String sslprotocols = "any";
    String sslciphers = "any";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = false;
    String cacheServerSslprotocols = "any";
    String cacheServerSslciphers = "any";
    boolean cacheServerSslRequireAuth = true;

    DistributionConfigImpl config = new DistributionConfigImpl( new Properties() );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), cacheServerSslenabled );
    isEqual( config.getServerSSLProtocols(), cacheServerSslprotocols );
    isEqual( config.getServerSSLCiphers(), cacheServerSslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), cacheServerSslRequireAuth );
  }
  
  @Test
  public void testGatewayDefaultConfig() throws Exception {
    boolean sslenabled = false;
    String sslprotocols = "any";
    String sslciphers = "any";
    boolean requireAuth = true;

    boolean gatewaySslenabled = false;
    String gatewaySslprotocols = "any";
    String gatewaySslciphers = "any";
    boolean gatewaySslRequireAuth = true;

    DistributionConfigImpl config = new DistributionConfigImpl( new Properties() );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), gatewaySslenabled );
    isEqual( config.getGatewaySSLProtocols(), gatewaySslprotocols );
    isEqual( config.getGatewaySSLCiphers(), gatewaySslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), gatewaySslRequireAuth );
  }
  

  @Test
  public void testManagerConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean jmxManagerSsl = true;
    boolean jmxManagerSslenabled = true;
    String  jmxManagerSslprotocols = "SSLv7";
    String  jmxManagerSslciphers = "RSA_WITH_GARBAGE";
    boolean jmxManagerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(JMX_MANAGER_SSL, String.valueOf(jmxManagerSsl));
    gemFireProps.put(JMX_MANAGER_SSL_ENABLED, String.valueOf(jmxManagerSslenabled));
    gemFireProps.put(JMX_MANAGER_SSL_PROTOCOLS, jmxManagerSslprotocols);
    gemFireProps.put(JMX_MANAGER_SSL_CIPHERS, jmxManagerSslciphers);
    gemFireProps.put(JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(jmxManagerSslRequireAuth));
    try{
      DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    }catch(IllegalArgumentException e){
      if (! e.toString().contains( "GemFire properties \'jmx-manager-ssl\' and \'jmx-manager-ssl-enabled\' can not be used at the same time")) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }
    
    gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(JMX_MANAGER_SSL, String.valueOf(jmxManagerSsl));
    gemFireProps.put(JMX_MANAGER_SSL_PROTOCOLS, jmxManagerSslprotocols);
    gemFireProps.put(JMX_MANAGER_SSL_CIPHERS, jmxManagerSslciphers);
    gemFireProps.put(JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(jmxManagerSslRequireAuth));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getJmxManagerSSLEnabled(), jmxManagerSslenabled );
    isEqual( config.getJmxManagerSSLProtocols(), jmxManagerSslprotocols );
    isEqual( config.getJmxManagerSSLCiphers(), jmxManagerSslciphers );
    isEqual( config.getJmxManagerSSLRequireAuthentication(), jmxManagerSslRequireAuth );
  }
  
  
  @Test
  public void testCacheServerConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = true;
    String  cacheServerSslprotocols = "SSLv7";
    String  cacheServerSslciphers = "RSA_WITH_GARBAGE";
    boolean cacheServerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(SERVER_SSL_ENABLED, String.valueOf(cacheServerSslenabled));
    gemFireProps.put(SERVER_SSL_PROTOCOLS, cacheServerSslprotocols);
    gemFireProps.put(SERVER_SSL_CIPHERS, cacheServerSslciphers);
    gemFireProps.put(SERVER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(cacheServerSslRequireAuth));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), cacheServerSslenabled );
    isEqual( config.getServerSSLProtocols(), cacheServerSslprotocols );
    isEqual( config.getServerSSLCiphers(), cacheServerSslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), cacheServerSslRequireAuth );
  }

  @Test
  public void testGatewayConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean gatewaySslenabled = true;
    String  gatewaySslprotocols = "SSLv7";
    String  gatewaySslciphers = "RSA_WITH_GARBAGE";
    boolean gatewaySslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(GATEWAY_SSL_ENABLED, String.valueOf(gatewaySslenabled));
    gemFireProps.put(GATEWAY_SSL_PROTOCOLS, gatewaySslprotocols);
    gemFireProps.put(GATEWAY_SSL_CIPHERS, gatewaySslciphers);
    gemFireProps.put(GATEWAY_SSL_REQUIRE_AUTHENTICATION, String.valueOf(gatewaySslRequireAuth));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), gatewaySslenabled );
    isEqual( config.getGatewaySSLProtocols(), gatewaySslprotocols );
    isEqual( config.getGatewaySSLCiphers(), gatewaySslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), gatewaySslRequireAuth );
  }
  
  @Test
  public void testCustomizedClusterSslConfig() throws Exception {
    
    boolean sslenabled = true;
    String  sslprotocols = "SSLv1";
    String  sslciphers = "RSA_WITH_NOTHING";
    boolean requireAuth = true;

    boolean clusterSslenabled = true;
    String  clusterSslprotocols = "SSLv7";
    String  clusterSslciphers = "RSA_WITH_GARBAGE";
    boolean clusterSslRequireAuth = true;
    
    //sslEnabled and clusterSSLEnabled set at the same time
    Properties gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_ENABLED, "false");
    DistributionConfigImpl config = null;
    try{
      config = new DistributionConfigImpl( gemFireProps );
      throw new Exception();
    }catch(IllegalArgumentException e){
      if (! e.toString().contains( "GemFire properties \'ssl-enabled\' and \'cluster-ssl-enabled\' can not be used at the same time")) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }
    
    //ssl-protocol and cluster-ssl-protocol set at the same time
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, clusterSslprotocols);
    try{
      config = new DistributionConfigImpl( gemFireProps );
      throw new Exception();
    }catch(IllegalArgumentException e){
      if (! e.toString().contains( "GemFire properties \'ssl-protocols\' and \'cluster-ssl-protocols\' can not be used at the same time") ) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }
    
    //ssl-protocol and cluster-ssl-protocol set at the same time with same value
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    try{
      config = new DistributionConfigImpl( gemFireProps );
    } catch(IllegalArgumentException e){
      throw new Exception();
    }
    
    //ssl-cipher and cluster-ssl-cipher set at the same time
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_CIPHERS, clusterSslciphers);
    try{
      config = new DistributionConfigImpl( gemFireProps );
      throw new Exception();
    } catch(IllegalArgumentException e){
      if (! e.toString().contains( "GemFire properties \'ssl-cipher\' and \'cluster-ssl-cipher\' can not be used at the same time") ) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }
    
    //ssl-cipher and cluster-ssl-cipher set at the same time with same value
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    try{
      config = new DistributionConfigImpl( gemFireProps );
    } catch(IllegalArgumentException e){
      throw new Exception();
    }
    
    //ssl-require-authentication and cluster-ssl-require-authentication set at the same time
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_REQUIRE_AUTHENTICATION, "true");
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, "false");
    try{
      config = new DistributionConfigImpl( gemFireProps );
      throw new Exception();
    } catch(IllegalArgumentException e){
      if (! e.toString().contains( "GemFire properties \'ssl-require-authentication\' and \'cluster-ssl-require-authentication\' can not be used at the same time") ) {
        throw new Exception( "did not get expected exception, got this instead...", e );
      }
    }
    
    //ssl-require-authentication and cluster-ssl-require-authentication set at the same time and have the same value
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, "true");
    gemFireProps.put(SSL_REQUIRE_AUTHENTICATION, "true");
    gemFireProps.put(CLUSTER_SSL_ENABLED, "true");
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, "true");
    try{
      config = new DistributionConfigImpl( gemFireProps );
    } catch(IllegalArgumentException e){
      throw new Exception();
    }
    
    // only ssl-* properties provided. same should reflect in cluster-ssl properties
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));
    gemFireProps.put(SSL_CIPHERS, sslciphers);
    gemFireProps.put(SSL_PROTOCOLS, sslprotocols);

    gemFireProps.putAll(getGfSecurityPropertiesSSL());
    
    config = new DistributionConfigImpl(gemFireProps);

    isEqual(sslenabled, config.getSSLEnabled());
    isEqual(sslprotocols, config.getSSLProtocols());
    isEqual(sslciphers, config.getSSLCiphers());
    isEqual(requireAuth, config.getSSLRequireAuthentication());

    isEqual(sslenabled, config.getClusterSSLEnabled());
    isEqual(sslprotocols, config.getClusterSSLProtocols());
    isEqual(sslciphers, config.getClusterSSLCiphers());
    isEqual(requireAuth, config.getClusterSSLRequireAuthentication());
    
    Properties sslProperties = config.getSSLProperties();
    isEqual( SSL_PROPS_MAP , sslProperties);

    Properties clusterSSLProperties = config.getClusterSSLProperties();
    isEqual( SSL_PROPS_MAP, clusterSSLProperties );
    
    //only clutser-ssl-properties provided.
    gemFireProps = new Properties();
    gemFireProps.setProperty(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(clusterSslenabled));
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(clusterSslRequireAuth));
    gemFireProps.put(CLUSTER_SSL_CIPHERS, clusterSslciphers);
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, clusterSslprotocols);

    gemFireProps.putAll(getGfSecurityPropertiesCluster(false));
    
    config = new DistributionConfigImpl(gemFireProps);

    isEqual(clusterSslenabled, config.getClusterSSLEnabled());
    isEqual(clusterSslprotocols, config.getClusterSSLProtocols());
    isEqual(clusterSslciphers, config.getClusterSSLCiphers());
    isEqual(clusterSslRequireAuth, config.getClusterSSLRequireAuthentication());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());
    
    clusterSSLProperties = config.getClusterSSLProperties();
    isEqual( SSL_PROPS_MAP, clusterSSLProperties );
    
  }
  
  @Test
  public void testCustomizedManagerSslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean jmxManagerSslenabled = true;
    String  jmxManagerSslprotocols = "SSLv7";
    String  jmxManagerSslciphers = "RSA_WITH_GARBAGE";
    boolean jmxManagerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(JMX_MANAGER_SSL_ENABLED, String.valueOf(jmxManagerSslenabled));
    gemFireProps.put(JMX_MANAGER_SSL_PROTOCOLS, jmxManagerSslprotocols);
    gemFireProps.put(JMX_MANAGER_SSL_CIPHERS, jmxManagerSslciphers);
    gemFireProps.put(JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(jmxManagerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesJMX(false /*partialJmxSslConfigOverride*/));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getJmxManagerSSLEnabled(), jmxManagerSslenabled );
    isEqual( config.getJmxManagerSSLProtocols(), jmxManagerSslprotocols );
    isEqual( config.getJmxManagerSSLCiphers(), jmxManagerSslciphers );
    isEqual( config.getJmxManagerSSLRequireAuthentication(), jmxManagerSslRequireAuth );

    isEqual(JMX_SSL_PROPS_MAP.get(JMX_MANAGER_SSL_KEYSTORE), config.getJmxManagerSSLKeyStore());
    isEqual(JMX_SSL_PROPS_MAP.get(JMX_MANAGER_SSL_KEYSTORE_TYPE), config.getJmxManagerSSLKeyStoreType());
    isEqual(JMX_SSL_PROPS_MAP.get(JMX_MANAGER_SSL_KEYSTORE_PASSWORD), config.getJmxManagerSSLKeyStorePassword());
    isEqual(JMX_SSL_PROPS_MAP.get(JMX_MANAGER_SSL_TRUSTSTORE), config.getJmxManagerSSLTrustStore());
    isEqual(JMX_SSL_PROPS_MAP.get(JMX_MANAGER_SSL_TRUSTSTORE_PASSWORD), config.getJmxManagerSSLTrustStorePassword());
  }
  
  @Test
  public void testCustomizedCacheServerSslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = true;
    String  cacheServerSslprotocols = "SSLv7";
    String  cacheServerSslciphers = "RSA_WITH_GARBAGE";
    boolean cacheServerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(SERVER_SSL_ENABLED, String.valueOf(cacheServerSslenabled));
    gemFireProps.put(SERVER_SSL_PROTOCOLS, cacheServerSslprotocols);
    gemFireProps.put(SERVER_SSL_CIPHERS, cacheServerSslciphers);
    gemFireProps.put(SERVER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(cacheServerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforCS(false));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), cacheServerSslenabled );
    isEqual( config.getServerSSLProtocols(), cacheServerSslprotocols );
    isEqual( config.getServerSSLCiphers(), cacheServerSslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), cacheServerSslRequireAuth );

    isEqual(SERVER_SSL_PROPS_MAP.get(SERVER_SSL_KEYSTORE), config.getServerSSLKeyStore());
    isEqual(SERVER_SSL_PROPS_MAP.get(SERVER_SSL_KEYSTORE_TYPE), config.getServerSSLKeyStoreType());
    isEqual(SERVER_SSL_PROPS_MAP.get(SERVER_SSL_KEYSTORE_PASSWORD), config.getServerSSLKeyStorePassword());
    isEqual(SERVER_SSL_PROPS_MAP.get(SERVER_SSL_TRUSTSTORE), config.getServerSSLTrustStore());
    isEqual(SERVER_SSL_PROPS_MAP.get(SERVER_SSL_TRUSTSTORE_PASSWORD), config.getServerSSLTrustStorePassword());
  }

  @Test
  public void testCustomizedGatewaySslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean gatewaySslenabled = true;
    String  gatewaySslprotocols = "SSLv7";
    String  gatewaySslciphers = "RSA_WITH_GARBAGE";
    boolean gatewaySslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(GATEWAY_SSL_ENABLED, String.valueOf(gatewaySslenabled));
    gemFireProps.put(GATEWAY_SSL_PROTOCOLS, gatewaySslprotocols);
    gemFireProps.put(GATEWAY_SSL_CIPHERS, gatewaySslciphers);
    gemFireProps.put(GATEWAY_SSL_REQUIRE_AUTHENTICATION, String.valueOf(gatewaySslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforGateway(false));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), gatewaySslenabled );
    isEqual( config.getGatewaySSLProtocols(), gatewaySslprotocols );
    isEqual( config.getGatewaySSLCiphers(), gatewaySslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), gatewaySslRequireAuth );

    isEqual(GATEWAY_SSL_PROPS_MAP.get(GATEWAY_SSL_KEYSTORE), config.getGatewaySSLKeyStore());
    isEqual(GATEWAY_SSL_PROPS_MAP.get(GATEWAY_SSL_KEYSTORE_TYPE), config.getGatewaySSLKeyStoreType());
    isEqual(GATEWAY_SSL_PROPS_MAP.get(GATEWAY_SSL_KEYSTORE_PASSWORD), config.getGatewaySSLKeyStorePassword());
    isEqual(GATEWAY_SSL_PROPS_MAP.get(GATEWAY_SSL_TRUSTSTORE), config.getGatewaySSLTrustStore());
    isEqual(GATEWAY_SSL_PROPS_MAP.get(GATEWAY_SSL_TRUSTSTORE_PASSWORD), config.getGatewaySSLTrustStorePassword());
    
  }
  
  @Test
  public void testPartialCustomizedManagerSslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean jmxManagerSslenabled = true;
    String  jmxManagerSslprotocols = "SSLv7";
    String  jmxManagerSslciphers = "RSA_WITH_GARBAGE";
    boolean jmxManagerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(JMX_MANAGER_SSL_ENABLED, String.valueOf(jmxManagerSslenabled));
    gemFireProps.put(JMX_MANAGER_SSL_PROTOCOLS, jmxManagerSslprotocols);
    gemFireProps.put(JMX_MANAGER_SSL_CIPHERS, jmxManagerSslciphers);
    gemFireProps.put(JMX_MANAGER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(jmxManagerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesJMX(true /*partialJmxSslConfigOverride*/));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getJmxManagerSSLEnabled(), jmxManagerSslenabled );
    isEqual( config.getJmxManagerSSLProtocols(), jmxManagerSslprotocols );
    isEqual( config.getJmxManagerSSLCiphers(), jmxManagerSslciphers );
    isEqual( config.getJmxManagerSSLRequireAuthentication(), jmxManagerSslRequireAuth );

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(JMX_SSL_PROPS_SUBSET_MAP.get(JMX_MANAGER_SSL_KEYSTORE), config.getJmxManagerSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getJmxManagerSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getJmxManagerSSLKeyStorePassword());
    isEqual(JMX_SSL_PROPS_SUBSET_MAP.get(JMX_MANAGER_SSL_TRUSTSTORE), config.getJmxManagerSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getJmxManagerSSLTrustStorePassword());
  }
  
  
  @Test
  public void testPartialCustomizedCacheServerSslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = true;
    String  cacheServerSslprotocols = "SSLv7";
    String  cacheServerSslciphers = "RSA_WITH_GARBAGE";
    boolean cacheServerSslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(SERVER_SSL_ENABLED, String.valueOf(cacheServerSslenabled));
    gemFireProps.put(SERVER_SSL_PROTOCOLS, cacheServerSslprotocols);
    gemFireProps.put(SERVER_SSL_CIPHERS, cacheServerSslciphers);
    gemFireProps.put(SERVER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(cacheServerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforCS(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), cacheServerSslenabled );
    isEqual( config.getServerSSLProtocols(), cacheServerSslprotocols );
    isEqual( config.getServerSSLCiphers(), cacheServerSslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), cacheServerSslRequireAuth );

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(SERVER_PROPS_SUBSET_MAP.get(SERVER_SSL_KEYSTORE), config.getServerSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getServerSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getServerSSLKeyStorePassword());
    isEqual(SERVER_PROPS_SUBSET_MAP.get(SERVER_SSL_TRUSTSTORE), config.getServerSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getServerSSLTrustStorePassword());
  }
  
  @Test
  public void testPartialCustomizedGatewaySslConfig() throws Exception {
    boolean sslenabled = false;
    String  sslprotocols = "any";
    String  sslciphers = "any";
    boolean requireAuth = true;

    boolean gatewaySslenabled = true;
    String  gatewaySslprotocols = "SSLv7";
    String  gatewaySslciphers = "RSA_WITH_GARBAGE";
    boolean gatewaySslRequireAuth = true;

    Properties gemFireProps = new Properties();
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.put(GATEWAY_SSL_ENABLED, String.valueOf(gatewaySslenabled));
    gemFireProps.put(GATEWAY_SSL_PROTOCOLS, gatewaySslprotocols);
    gemFireProps.put(GATEWAY_SSL_CIPHERS, gatewaySslciphers);
    gemFireProps.put(GATEWAY_SSL_REQUIRE_AUTHENTICATION, String.valueOf(gatewaySslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforGateway(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), gatewaySslenabled );
    isEqual( config.getGatewaySSLProtocols(), gatewaySslprotocols );
    isEqual( config.getGatewaySSLCiphers(), gatewaySslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), gatewaySslRequireAuth );

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(GATEWAY_PROPS_SUBSET_MAP.get(GATEWAY_SSL_KEYSTORE), config.getGatewaySSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getGatewaySSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getGatewaySSLKeyStorePassword());
    isEqual(GATEWAY_PROPS_SUBSET_MAP.get(GATEWAY_SSL_TRUSTSTORE), config.getGatewaySSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getGatewaySSLTrustStorePassword());

  }
  
  @Test
  public void testP2pSSLPropsOverriden_ServerPropsNotOverriden(){
    boolean sslenabled = true;
    String  sslprotocols = "overrriden";
    String  sslciphers = "overrriden";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = false;
    String  cacheServerSslprotocols = "SSLv7";
    String  cacheServerSslciphers = "RSA_WITH_GARBAGE";
    boolean cacheServerSslRequireAuth = false;

    Properties gemFireProps = new Properties();
    gemFireProps.put(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforCS(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), sslenabled );
    isEqual( config.getServerSSLProtocols(), sslprotocols );
    isEqual( config.getServerSSLCiphers(), sslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), requireAuth );
    
    assertFalse(config.getServerSSLEnabled()==cacheServerSslenabled);
    assertFalse(config.getServerSSLProtocols().equals(cacheServerSslprotocols));
    assertFalse(config.getServerSSLCiphers().equals(cacheServerSslciphers));
    assertFalse(config.getServerSSLRequireAuthentication()==cacheServerSslRequireAuth);
    
    System.out.println(config.toLoggerString());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getServerSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getServerSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getServerSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getServerSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getServerSSLTrustStorePassword());
    
  }
  
  @Test
  public void testP2pSSLPropsOverriden_ServerPropsOverriden(){
    boolean sslenabled = true;
    String  sslprotocols = "overrriden";
    String  sslciphers = "overrriden";
    boolean requireAuth = true;

    boolean cacheServerSslenabled = false;
    String  cacheServerSslprotocols = "SSLv7";
    String  cacheServerSslciphers = "RSA_WITH_GARBAGE";
    boolean cacheServerSslRequireAuth = false;

    Properties gemFireProps = new Properties();
    gemFireProps.put(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));
    
    gemFireProps.put(SERVER_SSL_ENABLED, String.valueOf(cacheServerSslenabled));
    gemFireProps.put(SERVER_SSL_PROTOCOLS, cacheServerSslprotocols);
    gemFireProps.put(SERVER_SSL_CIPHERS, cacheServerSslciphers);
    gemFireProps.put(SERVER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(cacheServerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforCS(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getServerSSLEnabled(), cacheServerSslenabled );
    isEqual( config.getServerSSLProtocols(), cacheServerSslprotocols );
    isEqual( config.getServerSSLCiphers(), cacheServerSslciphers );
    isEqual( config.getServerSSLRequireAuthentication(), cacheServerSslRequireAuth );
    
    assertFalse(config.getServerSSLEnabled()==sslenabled);
    assertFalse(config.getServerSSLProtocols().equals(sslprotocols));
    assertFalse(config.getServerSSLCiphers().equals(sslciphers));
    assertFalse(config.getServerSSLRequireAuthentication()==requireAuth);
    
    System.out.println(config.toLoggerString());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(SERVER_PROPS_SUBSET_MAP.get(SERVER_SSL_KEYSTORE), config.getServerSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getServerSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getServerSSLKeyStorePassword());
    isEqual(SERVER_PROPS_SUBSET_MAP.get(SERVER_SSL_TRUSTSTORE), config.getServerSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getServerSSLTrustStorePassword());
  }
  
  @Test
  public void testClusterSSLPropsOverriden_GatewayPropsNotOverriden(){
    boolean sslenabled = true;
    String  sslprotocols = "overrriden";
    String  sslciphers = "overrriden";
    boolean requireAuth = true;

    boolean gatewayServerSslenabled = false;
    String  gatewayServerSslprotocols = "SSLv7";
    String  gatewayServerSslciphers = "RSA_WITH_GARBAGE";
    boolean gatewayServerSslRequireAuth = false;

    Properties gemFireProps = new Properties();
    gemFireProps.put(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforGateway(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), sslenabled );
    isEqual( config.getGatewaySSLProtocols(), sslprotocols );
    isEqual( config.getGatewaySSLCiphers(), sslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), requireAuth );
    
    assertFalse(config.getGatewaySSLEnabled()==gatewayServerSslenabled);
    assertFalse(config.getGatewaySSLProtocols().equals(gatewayServerSslprotocols));
    assertFalse(config.getGatewaySSLCiphers().equals(gatewayServerSslciphers));
    assertFalse(config.getGatewaySSLRequireAuthentication()==gatewayServerSslRequireAuth);
    
    System.out.println(config.toLoggerString());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getGatewaySSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getGatewaySSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getGatewaySSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getGatewaySSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getGatewaySSLTrustStorePassword());
    
  }
  
  @Test
  public void testP2pSSLPropsOverriden_GatewayPropsOverriden(){
    boolean sslenabled = true;
    String  sslprotocols = "overrriden";
    String  sslciphers = "overrriden";
    boolean requireAuth = true;

    boolean gatewayServerSslenabled = false;
    String  gatewayServerSslprotocols = "SSLv7";
    String  gatewayServerSslciphers = "RSA_WITH_GARBAGE";
    boolean gatewayServerSslRequireAuth = false;

    Properties gemFireProps = new Properties();
    gemFireProps.put(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));
    
    gemFireProps.put(GATEWAY_SSL_ENABLED, String.valueOf(gatewayServerSslenabled));
    gemFireProps.put(GATEWAY_SSL_PROTOCOLS, gatewayServerSslprotocols);
    gemFireProps.put(GATEWAY_SSL_CIPHERS, gatewayServerSslciphers);
    gemFireProps.put(GATEWAY_SSL_REQUIRE_AUTHENTICATION, String.valueOf(gatewayServerSslRequireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesforGateway(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getGatewaySSLEnabled(), gatewayServerSslenabled );
    isEqual( config.getGatewaySSLProtocols(), gatewayServerSslprotocols );
    isEqual( config.getGatewaySSLCiphers(), gatewayServerSslciphers );
    isEqual( config.getGatewaySSLRequireAuthentication(), gatewayServerSslRequireAuth );
    
    System.out.println(config.toLoggerString());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(GATEWAY_PROPS_SUBSET_MAP.get(GATEWAY_SSL_KEYSTORE), config.getGatewaySSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getGatewaySSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getGatewaySSLKeyStorePassword());
    isEqual(GATEWAY_PROPS_SUBSET_MAP.get(GATEWAY_SSL_TRUSTSTORE), config.getGatewaySSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getGatewaySSLTrustStorePassword());
    
  }
  
  @Test
  public void testP2pSSLPropsOverriden_JMXPropsNotOverriden(){
    boolean sslenabled = true;
    String  sslprotocols = "overrriden";
    String  sslciphers = "overrriden";
    boolean requireAuth = true;

    boolean jmxManagerSslenabled = false;
    String  jmxManagerSslprotocols = "SSLv7";
    String  jmxManagerSslciphers = "RSA_WITH_GARBAGE";
    boolean jmxManagerSslRequireAuth = false;

    Properties gemFireProps = new Properties();
    gemFireProps.put(MCAST_PORT, "0");
    gemFireProps.put(CLUSTER_SSL_ENABLED, String.valueOf(sslenabled));
    gemFireProps.put(CLUSTER_SSL_PROTOCOLS, sslprotocols);
    gemFireProps.put(CLUSTER_SSL_CIPHERS, sslciphers);
    gemFireProps.put(CLUSTER_SSL_REQUIRE_AUTHENTICATION, String.valueOf(requireAuth));

    gemFireProps.putAll(getGfSecurityPropertiesJMX(true));

    DistributionConfigImpl config = new DistributionConfigImpl( gemFireProps );
    isEqual( config.getClusterSSLEnabled(), sslenabled );
    isEqual( config.getClusterSSLProtocols(), sslprotocols );
    isEqual( config.getClusterSSLCiphers(), sslciphers );
    isEqual( config.getClusterSSLRequireAuthentication(), requireAuth );

    isEqual( config.getJmxManagerSSLEnabled(), sslenabled );
    isEqual( config.getJmxManagerSSLProtocols(), sslprotocols );
    isEqual( config.getJmxManagerSSLCiphers(), sslciphers );
    isEqual( config.getJmxManagerSSLRequireAuthentication(), requireAuth );
    
    assertFalse(config.getJmxManagerSSLEnabled()==jmxManagerSslenabled);
    assertFalse(config.getJmxManagerSSLProtocols().equals(jmxManagerSslprotocols));
    assertFalse(config.getJmxManagerSSLCiphers().equals(jmxManagerSslciphers));
    assertFalse(config.getJmxManagerSSLRequireAuthentication()==jmxManagerSslRequireAuth);
    
    System.out.println(config.toLoggerString());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getClusterSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getClusterSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getClusterSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getClusterSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getClusterSSLTrustStorePassword());

    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE), config.getJmxManagerSSLKeyStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_TYPE), config.getJmxManagerSSLKeyStoreType());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_KEYSTORE_PASSWORD), config.getJmxManagerSSLKeyStorePassword());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE), config.getJmxManagerSSLTrustStore());
    isEqual(CLUSTER_SSL_PROPS_MAP.get(CLUSTER_SSL_TRUSTSTORE_PASSWORD), config.getJmxManagerSSLTrustStorePassword());
    
  }
  
  private static Properties getGfSecurityPropertiesSSL() {
    Properties gfSecurityProps = new Properties();

    Set<Entry<Object, Object>> entrySet = SSL_PROPS_MAP.entrySet();
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    return gfSecurityProps;
  }
  
  private static Properties getGfSecurityPropertiesCluster(boolean partialClusterSslConfigOverride) {
    Properties gfSecurityProps = new Properties();

    Set<Entry<Object, Object>> entrySet = SSL_PROPS_MAP.entrySet();
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    if (partialClusterSslConfigOverride) {
      entrySet = CLUSTER_SSL_PROPS_SUBSET_MAP.entrySet();
    } else {
      entrySet = CLUSTER_SSL_PROPS_MAP.entrySet();
    }
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }
    return gfSecurityProps;
  }
  
  private static Properties getGfSecurityPropertiesJMX(boolean partialJmxSslConfigOverride) {
    Properties gfSecurityProps = new Properties();

    Set<Entry<Object, Object>> entrySet = CLUSTER_SSL_PROPS_MAP.entrySet();
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    if (partialJmxSslConfigOverride) {
      entrySet = JMX_SSL_PROPS_SUBSET_MAP.entrySet();
    } else {
      entrySet = JMX_SSL_PROPS_MAP.entrySet();
    }
    for (Entry<Object, Object> entry : entrySet) {
      // Add "-jmx" suffix for JMX Manager properties.
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    return gfSecurityProps;
  }
  
  private static Properties getGfSecurityPropertiesforCS(boolean partialCSSslConfigOverride) {
    Properties gfSecurityProps = new Properties();

    Set<Entry<Object, Object>> entrySet = CLUSTER_SSL_PROPS_MAP.entrySet();
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    if (partialCSSslConfigOverride) {
      entrySet = SERVER_PROPS_SUBSET_MAP.entrySet();
    } else {
      entrySet = SERVER_SSL_PROPS_MAP.entrySet();
    }
    for (Entry<Object, Object> entry : entrySet) {
      // Add "-cacheserver" suffix for CacheServer properties.
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }
    gfSecurityProps.list(System.out);
    return gfSecurityProps;
  }

  private static Properties getGfSecurityPropertiesforGateway(boolean partialGatewaySslConfigOverride) {
    Properties gfSecurityProps = new Properties();

    Set<Entry<Object, Object>> entrySet = CLUSTER_SSL_PROPS_MAP.entrySet();
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }

    if (partialGatewaySslConfigOverride) {
      entrySet = GATEWAY_PROPS_SUBSET_MAP.entrySet();
    } else {
      entrySet = GATEWAY_SSL_PROPS_MAP.entrySet();
    }
    for (Entry<Object, Object> entry : entrySet) {
      gfSecurityProps.put(entry.getKey(), entry.getValue());
    }
    gfSecurityProps.list(System.out);
    return gfSecurityProps;
  }
  
  public void isEqual( boolean a, boolean e ) throws AssertionFailedError {
    assertEquals( a, e );
  }
  
  public void isEqual( Object a, Object e ) throws AssertionFailedError {
    assertEquals( a, e );
  } 
  
}
