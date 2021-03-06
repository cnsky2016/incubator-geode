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

package com.gemstone.gemfire.internal.cache;


/**
 * A factory that produces RegionEntry instances.
 *
 * @since GemFire 3.5.1
 *
 *
 */
public interface RegionEntryFactory {
  /**
   * Creates an instance of RegionEntry.
   * @return the created entry
   */
  public RegionEntry createEntry(RegionEntryContext context, Object key, Object value);
  /**
   * @return the Class that each entry, of this factory, is an instance of
   */
  public Class getEntryClass();
  /**
   * @return return the versioned equivalent of this RegionEntryFactory
   */
  public RegionEntryFactory makeVersioned();
  
  /**
   * Return the equivalent on heap version of this entry factory. This
   * is used for creating temporary region entries that shouldn't be stored
   * off heap.
   */
  public RegionEntryFactory makeOnHeap();
}
