/*
 * Copyright (c) 2001 - 2009 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import junit.framework.Test;

import org.geotools.data.complex.AppSchemaDataAccess;
import org.w3c.dom.Document;

/**
 * WFS GetFeature to test integration of {@link AppSchemaDataAccess} with GeoServer.
 * 
 * @author Niels Charlier
 */
public class LocalResolveTest extends AbstractAppSchemaWfsTestSupport {

    /**
     * Read-only test so can use one-time setup.
     * 
     * @return
     */
    public static Test suite() {
        return new OneTimeTestSetup(new LocalResolveTest());
    }

    @Override
    protected NamespaceTestData buildTestData() {
        return new LocalResolveMockData();
    }
    
    /**
     * Test Local Resolve with Depth 2.
     */
    public void testResolveDepth2() {
    	
    	Document doc = getAsDOM("wfs?request=GetFeature&version=2.0.0&typename=gsml:MappedFeature&resolve=local&resolveDepth=2");
    	
    	LOGGER.info("WFS testGetFeatureContent response:\n" + prettyString(doc));

    	assertXpathEvaluatesTo("gu.25699", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/gsml:GeologicUnit/@gml:id", doc);
    	assertXpathEvaluatesTo("urn:ogc:def:nil:OGC::unknown", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/gsml:GeologicUnit/gsml:composition/gsml:CompositionPart/gsml:role/@xlink:href", doc);
    	assertXpathCount(3, "//gsml:GeologicUnit", doc);
    	assertXpathCount(3, "//gsml:CompositionPart", doc);
        
    }
    
    /**
     * Test Local Resolve with Depth 1.
     */
    public void testResolveDepth1() {
    	
    	Document doc = getAsDOM("wfs?request=GetFeature&version=2.0.0&typename=gsml:MappedFeature&resolve=local&resolveDepth=1");
    	
    	LOGGER.info("WFS testGetFeatureContent response:\n" + prettyString(doc));

    	assertXpathEvaluatesTo("gu.25699", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/gsml:GeologicUnit/@gml:id", doc);
    	assertXpathEvaluatesTo("urn:x-test:CompositionPart:cp.167775491936278899", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/gsml:GeologicUnit/gsml:composition/@xlink:href", doc);
    	assertXpathCount(3, "//gsml:GeologicUnit", doc);
    	assertXpathCount(0, "//gsml:CompositionPart", doc);
        
    }
    
    /**
     * Test Local Resolve with Depth 0.
     */
    public void testResolveDepth0() {
    	
    	Document doc = getAsDOM("wfs?request=GetFeature&version=2.0.0&typename=gsml:MappedFeature&resolve=local&resolveDepth=0");
    	
    	LOGGER.info("WFS testGetFeatureContent response:\n" + prettyString(doc));

    	assertXpathEvaluatesTo("urn:x-test:GeologicUnit:gu.25699", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/@xlink:href", doc);
    	assertXpathCount(0, "//gsml:GeologicUnit", doc);
    	assertXpathCount(0, "//gsml:CompositionPart", doc);
        
    }
    
    /**
     * Test Local Resolve is not applied when turned off
     */
    public void testNoResolve() {
    	
    	Document doc = getAsDOM("wfs?request=GetFeature&version=2.0.0&typename=gsml:MappedFeature&resolve=none");
    	
    	LOGGER.info("WFS testGetFeatureContent response:\n" + prettyString(doc));

    	assertXpathEvaluatesTo("urn:x-test:GeologicUnit:gu.25699", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/@xlink:href", doc);
    	assertXpathCount(0, "//gsml:GeologicUnit", doc);
    	assertXpathCount(0, "//gsml:CompositionPart", doc);
        
    }
    
    /**
     * Test Local Resolve Time Out 
     */
    public void testResolveTimeOut() {
    	
    	//the only thing we can test with 100% certainty is resolve time out = 0
    	Document doc = getAsDOM("wfs?request=GetFeature&version=2.0.0&typename=gsml:MappedFeature&resolve=local&resolveDepth=2&resolveTimeOut=0");
    	
    	LOGGER.info("WFS testGetFeatureContent response:\n" + prettyString(doc));

    	assertXpathEvaluatesTo("urn:x-test:GeologicUnit:gu.25699", "//gsml:MappedFeature[@gml:id='mf1']/gsml:specification/@xlink:href", doc);
    	assertXpathCount(0, "//gsml:GeologicUnit", doc);
    	assertXpathCount(0, "//gsml:CompositionPart", doc);
        
    }
    

}
