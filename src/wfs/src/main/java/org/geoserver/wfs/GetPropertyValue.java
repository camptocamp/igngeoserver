/* Copyright (c) 2001 - 2011 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.wfs20.FeatureCollectionType;
import net.opengis.wfs20.GetFeatureType;
import net.opengis.wfs20.GetPropertyValueType;
import net.opengis.wfs20.QueryType;
import net.opengis.wfs20.ValueCollectionType;
import net.opengis.wfs20.Wfs20Factory;

import org.eclipse.emf.ecore.EObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.NamespaceInfo;
import org.geoserver.wfs.request.GetFeatureRequest;
import org.geoserver.wfs.request.Query;
import org.geotools.filter.IsNullImpl;
import org.geotools.wfs.PropertyValueCollection;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.xml.sax.helpers.NamespaceSupport;

public class GetPropertyValue {
	
    GetFeature delegate;
    Catalog catalog;
    FilterFactory2 filterFactory;
	
	public class FilteredQuery extends Query.WFS20 {
		Filter filter;
        
        public FilteredQuery(EObject adaptee, Filter filter) {
            super(adaptee);
			this.filter = filter;
        }
                
        @Override
        public Filter getFilter() {
            Filter originalFilter = super.getFilter();
            if (originalFilter == null) {
            	return filter;
            } else {
            	return filterFactory.and(originalFilter, filter);
            }
        }        
    }
	 	    
	public class FilteredRequest extends GetFeatureRequest.WFS20 {
		Filter filter;
		
		public FilteredRequest(EObject adaptee, Filter filter) {
			super(adaptee);
			this.filter = filter;
		}

		@Override
		public List<Query> getQueries() {
			List<Query> list = new ArrayList<Query>();
			for (Object o : getAdaptedQueries()) {
				list.add(new FilteredQuery((EObject) o, filter));
			}

			return list;
		}
	}
	
	//HACK HACK HACK
	public static class IsCompletelyNullImpl extends IsNullImpl {
		public IsCompletelyNullImpl(FilterFactory factory, Expression expression) {
			super(factory, expression);
		}
		
		protected Object eval(org.opengis.filter.expression.Expression expression, Object object) {
			if( expression == null ) return null;
			return expression.evaluate( object );
		}		
		
	    public Object accept(FilterVisitor visitor, Object extraData) {
	        Object o = visitor.visit(this, extraData);
	        if (o instanceof IsNullImpl) {
	        	return new IsCompletelyNullImpl(factory, ((IsNullImpl)o).getExpression());
	        }
	        return o;
	    }
	}
	    
    public GetPropertyValue(WFSInfo info, Catalog catalog, FilterFactory2 filterFactory) {
        delegate = new GetFeature(info, catalog);
        delegate.setFilterFactory(filterFactory);
        
        this.catalog = catalog;
        this.filterFactory = filterFactory;
    }
    
    /**      
     * @return NamespaceSupport from Catalog
     */
    public NamespaceSupport getNamespaceSupport() {
        NamespaceSupport ns = new NamespaceSupport();
        Iterator<NamespaceInfo> it = catalog.getNamespaces().iterator();
        while (it.hasNext()) {
            NamespaceInfo ni = it.next();
            ns.declarePrefix(ni.getPrefix(), ni.getURI());
        }
        return ns;
    }

    public ValueCollectionType run(GetPropertyValueType request) throws WFSException {

        if (request.getValueReference() == null) {
            throw new WFSException(request, "No valueReference specified", "MissingParameterValue")
                .locator("valueReference");
        }
        
        //do a getFeature request
        GetFeatureType getFeature = Wfs20Factory.eINSTANCE.createGetFeatureType();
        getFeature.getAbstractQueryExpression().add(request.getAbstractQueryExpression());
        getFeature.setResolve(request.getResolve());
        getFeature.setResolveDepth(request.getResolveDepth());
        getFeature.setResolveTimeout(request.getResolveTimeout());
        getFeature.setCount(request.getCount());
        
        PropertyName propertyName = filterFactory.property(request.getValueReference(), getNamespaceSupport());    	
        Filter filter = filterFactory.not(new IsCompletelyNullImpl(filterFactory, propertyName));

        FeatureCollectionType fc = (FeatureCollectionType) 
            delegate.run(new FilteredRequest(getFeature, filter)).getAdaptee();
        //    delegate.run(GetFeatureRequest.adapt(getFeature)).getAdaptee();

        QueryType query = (QueryType) request.getAbstractQueryExpression();
        QName typeName = (QName) query.getTypeNames().iterator().next();
        FeatureTypeInfo featureType = 
           catalog.getFeatureTypeByName(typeName.getNamespaceURI(), typeName.getLocalPart());

        try {           
        	PropertyName propertyNameNoIndexes = filterFactory.property(request.getValueReference().replaceAll("\\[.*\\]", ""), getNamespaceSupport());
            AttributeDescriptor descriptor = (AttributeDescriptor) propertyNameNoIndexes.evaluate(featureType.getFeatureType());
            if (descriptor == null) {
            	throw new WFSException(request, "No such attribute: " + request.getValueReference());
            }
           
            //create value collection type from feature collection
            ValueCollectionType vc = Wfs20Factory.eINSTANCE.createValueCollectionType();
            vc.setTimeStamp(fc.getTimeStamp());
            vc.setNumberMatched(fc.getNumberMatched());
            vc.setNumberReturned(fc.getNumberReturned());
            vc.getMember().add(new PropertyValueCollection(fc.getMember().iterator().next(), 
                descriptor, propertyName));
            return vc;
        }
        catch(IOException e) {
            throw new WFSException(request, e);
        }
    }

	public void setFilterFactory(FilterFactory2 filterFactory) {
		this.filterFactory = filterFactory;
		
	}
}
