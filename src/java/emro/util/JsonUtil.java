package emro.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class JsonUtil {
	
	/**
     * Map을 json으로 변환한다.
     *
     * @param map Map<String, Object>.
     * @return JSONObject.
     */
	public static JSONObject getJsonStringFromMap(Map<String, Object> map) {
		JSONObject jsonObject = new JSONObject();
        for( Map.Entry<String, Object> entry : map.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            jsonObject.put(key, value);
        }
        
        return jsonObject;
	}
	
	
	/**
     * List<Map>을 jsonArray로 변환한다.
     *
     * @param list List<Map<String, Object>>.
     * @return JSONArray.
     */
	public static JSONArray getJsonArrayFromList(List<Map> list) {
		JSONArray jsonArray = new JSONArray();
        for( Map<String, Object> map : list ) {
            jsonArray.add( getJsonStringFromMap( map ) );
        }
        return jsonArray;
	}
	
	
	/**
     * List<Map>을 jsonString으로 변환한다.
     *
     * @param list List<Map<String, Object>>.
     * @return String.
     */
	public static String getJsonStringFromList( List<Map> list )
    {
        JSONArray jsonArray = getJsonArrayFromList( list );
        return jsonArray.toJSONString();
    }


	/**
     * JsonObject를 Map<String, String>으로 변환한다.
     *
     * @param jsonObj JSONObject.
     * @return Map<String, Object>.
     */
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromJsonObject( org.json.JSONObject jsonObj ){
        Map<String, Object> map = new HashMap<String, Object>();
        
        try {
            Iterator<String> keys = jsonObj.keys();
            while(keys.hasNext()) {
            	String key = (String) keys.next();
            	String value = jsonObj.getString(key);
            	map.put(key, value);
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
 
        return map;
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromString(String json) {
		Map<String, Object> map = null;
		try {
			map = new ObjectMapper().readValue(json, Map.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("getMapFromString error");
			e.printStackTrace();
		}
		
		return map;
	}
	
	
	/**
     * JsonArray를 List<Map<String, String>>으로 변환한다.
     *
     * @param jsonArray JSONArray.
     * @return List<Map<String, Object>>.
     */
	public static List<Map<String, Object>> getListMapFromJsonArray( org.json.JSONArray jsonArray )
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        if( jsonArray != null )
        {
            int jsonSize = jsonArray.size();
            for( int i = 0; i < jsonSize; i++ )
            {
                Map<String, Object> map = JsonUtil.getMapFromJsonObject( ( org.json.JSONObject ) jsonArray.get(i) );
                list.add( map );
            }
        }
        
        return list;
    }


}
