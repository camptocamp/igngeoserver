/*
 * Copyright (c) 2001 - 2009 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import junit.framework.Test;

import org.geotools.data.complex.AppSchemaDataAccess;

/**
 * WFS GetFeature to test integration of {@link AppSchemaDataAccess} with GeoServer.
 * 
 * @author Niels Charlier
 */
public class LocalResolveWFeatureChainingTest extends LocalResolveTest {

    /**
     * Read-only test so can use one-time setup.
     * 
     * @return
     */
    public static Test suite() {
        return new OneTimeTestSetup(new LocalResolveWFeatureChainingTest());
    }

    @Override
    protected NamespaceTestData buildTestData() {
        return new LocalResolveWFeatureChainingMockData();
    }
    
    /**
     * Test Local Resolve Time Out 
     */
    public void testResolveTimeOut() {
    	
    	//not supported in this case
        
    }
    

}
