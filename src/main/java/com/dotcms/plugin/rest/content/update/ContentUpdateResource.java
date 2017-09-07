package com.dotcms.plugin.rest.content.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dotcms.contenttype.model.field.Field;
import com.dotcms.contenttype.model.type.ContentType;
import com.dotcms.contenttype.transform.field.LegacyFieldTransformer;
import com.dotcms.repackage.javax.ws.rs.Consumes;
import com.dotcms.repackage.javax.ws.rs.GET;
import com.dotcms.repackage.javax.ws.rs.PUT;
import com.dotcms.repackage.javax.ws.rs.Path;
import com.dotcms.repackage.javax.ws.rs.Produces;
import com.dotcms.repackage.javax.ws.rs.core.Context;
import com.dotcms.repackage.javax.ws.rs.core.MediaType;
import com.dotcms.repackage.javax.ws.rs.core.Response;
import com.dotcms.repackage.javax.ws.rs.core.Response.ResponseBuilder;
import com.dotcms.repackage.org.glassfish.jersey.server.JSONP;
import com.dotcms.rest.InitDataObject;
import com.dotcms.rest.WebResource;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.json.JSONArray;
import com.dotmarketing.util.json.JSONException;
import com.dotmarketing.util.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.liferay.portal.model.User;




/**
 * 
 * 
 * Call 
 *
 */
@Path("/contentUpdate")
public class ContentUpdateResource  {

    private final WebResource webResource = new WebResource();


    @PUT
    @JSONP
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadJson(@Context HttpServletRequest request,UpdateRequest updateRequest) throws DotStateException,
            DotDataException, DotSecurityException, JsonProcessingException {
        // force authentication
        InitDataObject auth = webResource.init(false, request, false);
        User user = auth.getUser();
        ContentletAPI conAPI = APILocator.getContentletAPIImpl();

        List<Contentlet> cons = APILocator.getContentletAPIImpl().search(updateRequest.query, 1000, 0,null, user, false);
        List<Map<String,String>> success = new ArrayList<>();
        List<Map<String,String>> failures = new ArrayList<>();
        for(Contentlet con : cons){
        	
        	ContentType type=APILocator.getContentTypeAPI(user).find(con.getContentTypeId());
        	Field field = type.fieldMap().get(updateRequest.field);
        	
        	if(field!=null){
        		Contentlet updateMe = conAPI.checkout(con.getInode(), user, false);
        		updateMe.getMap().put(Contentlet.DONT_VALIDATE_ME, true);
        		conAPI.setContentletProperty(updateMe, new LegacyFieldTransformer(field).asOldField(), updateRequest.value);
        		updateMe = conAPI.checkin(updateMe, user, false);
        		if(updateRequest.publish){
        			conAPI.publish(updateMe, user, false);
        			conAPI.unlock(updateMe, user, false);
        		}
        		success.add(ImmutableMap.of("id",updateMe.getIdentifier(),"inode",updateMe.getInode()));
        	}
        	else{
        		failures.add(ImmutableMap.of("id",con.getIdentifier(),"inode",con.getInode()));
        	}
        }
        JSONObject jo = new JSONObject();
        try {
			jo.put("success", new JSONArray(success));
			jo.put("failures", new JSONArray(failures));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        ResponseBuilder builder = Response.ok(jo.toString(), MediaType.APPLICATION_JSON);

        builder.header("Access-Control-Expose-Headers", "Authorization");
        builder.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        
        return builder.build();

    }
  
    @GET
    @JSONP
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@Context HttpServletRequest request) throws DotStateException,
            DotDataException, DotSecurityException, JsonProcessingException {
        // force authentication
        InitDataObject auth = webResource.init(false, request, false);
        User user = auth.getUser();
        

        
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        
        
        String json = ow.writeValueAsString("ok");
        ResponseBuilder builder = Response.ok(json, MediaType.APPLICATION_JSON);

        builder.header("Access-Control-Expose-Headers", "Authorization");
        builder.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        
        return builder.build();

    }
  

}