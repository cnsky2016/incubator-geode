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

import com.gemstone.gemfire.cache.*;
import com.gemstone.gemfire.cache30.CacheTestCase;
import com.gemstone.gemfire.internal.cache.DiskStoreImpl;
import com.gemstone.gemfire.pdx.PdxInstance;
import com.gemstone.gemfire.pdx.PdxReader;
import com.gemstone.gemfire.pdx.PdxSerializable;
import com.gemstone.gemfire.pdx.PdxWriter;
import com.gemstone.gemfire.pdx.internal.EnumInfo;
import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;
import com.gemstone.gemfire.pdx.internal.PdxType;
import com.gemstone.gemfire.test.dunit.Host;
import com.gemstone.gemfire.test.dunit.LogWriterUtils;
import com.gemstone.gemfire.test.dunit.SerializableCallable;
import com.gemstone.gemfire.test.dunit.VM;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.gemstone.gemfire.distributed.DistributedSystemConfigProperties.*;

public class PdxRenameDUnitTest  extends CacheTestCase{
  final List<String> filesToBeDeleted = new CopyOnWriteArrayList<String>();
  
  public PdxRenameDUnitTest(String name) {
    super(name);
  }
  
  public void testPdxRenameVersioning() throws Exception {
    final String DS_NAME = "PdxRenameDUnitTestDiskStore";
    final String DS_NAME2 = "PdxRenameDUnitTestDiskStore2";
    final int[] locatorPorts = AvailablePortHelper.getRandomAvailableTCPPorts(2);
    final File f = new File(DS_NAME);
    f.mkdir();
    final File f2 = new File(DS_NAME2);
    f2.mkdir();
    this.filesToBeDeleted.add(DS_NAME);
    this.filesToBeDeleted.add(DS_NAME2);
    
    final Properties props = new Properties();
    props.setProperty(MCAST_PORT, "0");
    props.setProperty(LOCATORS, "localhost[" + locatorPorts[0] + "],localhost[" + locatorPorts[1] + "]");
    props.setProperty(ENABLE_CLUSTER_CONFIGURATION, "false");
    
    Host host = Host.getHost(0);
    VM vm1 = host.getVM(0);
    VM vm2 = host.getVM(1);
    
    vm1.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        disconnectFromDS();
        props.setProperty(START_LOCATOR, "localhost[" + locatorPorts[0] + "]");
        final Cache cache = (new CacheFactory(props)).setPdxPersistent(true).setPdxDiskStore(DS_NAME).create();
        DiskStoreFactory dsf = cache.createDiskStoreFactory();
        dsf.setDiskDirs(new File[]{f});
        dsf.create(DS_NAME);
        RegionFactory<String, PdxValue> rf1 = cache.createRegionFactory(RegionShortcut.REPLICATE_PERSISTENT);    
        rf1.setDiskStoreName(DS_NAME);
        Region<String, PdxValue> region1 = rf1.create("region1");
        region1.put("key1", new PdxValue(1));
        return null;
      }
    });
    
    vm2.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        disconnectFromDS();
        props.setProperty(START_LOCATOR, "localhost[" + locatorPorts[1] + "]");
        final Cache cache = (new CacheFactory(props)).setPdxReadSerialized(true).setPdxPersistent(true).setPdxDiskStore(DS_NAME2).create();
        DiskStoreFactory dsf = cache.createDiskStoreFactory();
        dsf.setDiskDirs(new File[]{f2});
        dsf.create(DS_NAME2);
        RegionFactory rf1 = cache.createRegionFactory(RegionShortcut.REPLICATE_PERSISTENT);    
        rf1.setDiskStoreName(DS_NAME2);
        Region region1 = rf1.create("region1");
        Object v = region1.get("key1");
        assertNotNull(v);
        cache.close();
        return null;
      }
    });
    
    vm1.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        Cache cache = CacheFactory.getAnyInstance();
        if(cache != null && !cache.isClosed()) {
          cache.close();
        }
        return null;
      }
    });
    
    vm1.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        Collection<Object> renameResults = DiskStoreImpl.pdxRename(DS_NAME, new File[]{f}, "gemstone", "pivotal");
        assertEquals(2, renameResults.size());
        
        for(Object o : renameResults) {
          if(o instanceof PdxType) {
            PdxType t = (PdxType)o;
            assertEquals("com.pivotal.gemfire.internal.PdxRenameDUnitTest$PdxValue", t.getClassName());
          } else {
            EnumInfo ei = (EnumInfo) o;
            assertEquals("com.pivotal.gemfire.internal.PdxRenameDUnitTest$Day", ei.getClassName());
          }
        }
        return null;
      }
    });
    
    vm1.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        props.setProperty(START_LOCATOR, "localhost[" + locatorPorts[0] + "]");
        final Cache cache = (new CacheFactory(props)).setPdxPersistent(true).setPdxDiskStore(DS_NAME).create();
        DiskStoreFactory dsf = cache.createDiskStoreFactory();
        dsf.setDiskDirs(new File[]{f});
        dsf.create(DS_NAME);
        RegionFactory<String, PdxValue> rf1 = cache.createRegionFactory(RegionShortcut.REPLICATE_PERSISTENT);    
        rf1.setDiskStoreName(DS_NAME);
        Region<String, PdxValue> region1 = rf1.create("region1");
        return null;
      }
    });
    
    vm2.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        disconnectFromDS();
        props.setProperty(START_LOCATOR, "localhost[" + locatorPorts[1] + "]");
        final Cache cache = (new CacheFactory(props)).setPdxReadSerialized(true).setPdxPersistent(true).setPdxDiskStore(DS_NAME2).create();
        
        DiskStoreFactory dsf = cache.createDiskStoreFactory();
        dsf.setDiskDirs(new File[]{f2});
        dsf.create(DS_NAME2);
        RegionFactory rf1 = cache.createRegionFactory(RegionShortcut.REPLICATE_PERSISTENT);    
        rf1.setDiskStoreName(DS_NAME2);
        Region region1 = rf1.create("region1");
        PdxInstance v = (PdxInstance) region1.get("key1");
        assertNotNull(v);
        assertEquals("com.pivotal.gemfire.internal.PdxRenameDUnitTest$PdxValue", ((PdxInstanceImpl)v).getClassName());
        cache.close();
        return null;
      }
    });
    
    vm1.invoke(new SerializableCallable() {
      public Object call() throws Exception {
        Cache cache = CacheFactory.getAnyInstance();
        if(cache != null && !cache.isClosed()) {
          cache.close();
        }
        return null;
      }
    });
  }

  @Override
  public void preTearDownCacheTestCase() throws Exception {
    for (String path : this.filesToBeDeleted) {
      try {
        FileUtil.delete(new File(path));
      } catch (IOException e) {
        LogWriterUtils.getLogWriter().error("Unable to delete file", e);
      }
    }
    this.filesToBeDeleted.clear();
  }
  
  enum Day {
    Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
  }
  
  class PdxValue implements PdxSerializable {
    private int value;
    public Day aDay;
    public PdxValue(int v) {
      this.value = v;
      aDay = Day.Sunday;
    }

    @Override
    public void toData(PdxWriter writer) {
      writer.writeInt("value", this.value);
      writer.writeObject("aDay", aDay);
    }

    @Override
    public void fromData(PdxReader reader) {
      this.value = reader.readInt("value");
      this.aDay = (Day) reader.readObject("aDay");
    }
  }
}
