/* 
 * Copyright (c) 2001 - 2009 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.test;

import org.geoserver.data.test.MockData;
import org.geotools.data.complex.AppSchemaDataAccess;

/**
 * Mock data for testing integration of {@link AppSchemaDataAccess} with GeoServer.
 * 
 * Inspired by {@link MockData}.
 * 
 * @author Niels Charlier
 */
public class LocalResolveWFeatureChainingMockData extends AbstractAppSchemaMockData {

    /**
     * @see org.geoserver.test.AbstractAppSchemaMockData#addContent()
     */
    @Override
    public void addContent() {

    	putNamespace("wfs", "http://www.opengis.net/wfs/2.0");
    	putNamespace("gml", "http://www.opengis.net/gml/3.2");
    	putNamespace(GSML_PREFIX, "urn:cgi:xmlns:CGI:GeoSciML-Core:3.0.0");
    	
        addFeatureType(GSML_PREFIX, "MappedFeature", "MappedFeatureXlink32_v2.xml",
                "MappedFeaturePropertyfile.properties");
        addFeatureType(GSML_PREFIX, "GeologicUnit", "GeologicUnitXLink32_v2.xml", "GeologicUnit.properties");
        addFeatureType(GSML_PREFIX, "CompositionPart", "CompositionPartXLink32.xml", "CompositionPart.properties");
    }

}
