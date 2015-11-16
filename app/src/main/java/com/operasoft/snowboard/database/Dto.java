package com.operasoft.snowboard.database;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention; 
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.operasoft.snowboard.util.JSONParser;

import android.database.Cursor;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface Column {
   String name();
}

abstract public class Dto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int CLEAR = 0;
	public static final int DIRTY = 1;
	public static final int FAILED = 3;
	
	@Column(name="id")
	protected String id = null;

	protected String newId = null; // Snowboard only flag to store the new ID sent by Snowman for this DTO.
	
	@Column(name="created")
	protected String created  = null;
	
	@Column(name="modified")
	protected String modified = null;
	
	@Column(name="sync_flag")
	protected int    syncFlag = -1; // Snowboard only flag to indicate we need to push an update to the server
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCreated() {
		return created;
	}
	
	public void setCreated(String created) {
		this.created = created;
	}
	
	public String getModified() {
		return modified;
	}
	
	public void setModified(String modified) {
		this.modified = modified;
	}
	
	public int getSyncFlag() {
		return syncFlag;
	}
	
	public void setSyncFlag(int syncFlag) {
		this.syncFlag = syncFlag;
	}
	
	public boolean isNew() {
		return ( (created == null) || (id == null) );
	}
	
	public boolean isDirty() {
		return (syncFlag >= DIRTY) && (syncFlag < FAILED);
	}

	public String getNewId() {
		return newId;
	}

	public void setNewId(String newId) {
		this.newId = newId;
	}

	public void fillDto(Cursor cursor) {
		
	}
	
	public void fillDto(JSONParser jsonParser, JSONObject json) {
		
	}
	
	/**
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * 
	 */
	public void prepareDbFields(List<String> names, List<Object> values) throws Exception {
		for (Field f: Dto.class.getDeclaredFields()) {
			Column column = f.getAnnotation(Column.class);
			if (column != null) {
				names.add(column.name());
				Object value = f.get(this);
				if (value != null) {
					if ( value instanceof String ) {
						// Make sure to escape the '
						String text = ((String) value).replace("'", "''");
						values.add("'" + text + "'");
					} else {
						values.add(value);
					}
				} else {
					values.add("NULL");
				}
			}
		}
		
		for (Field f: this.getClass().getDeclaredFields()) {
		   Column column = f.getAnnotation(Column.class);
		   if (column != null) {
			   names.add(column.name());
			   String fieldName = f.getName();
			   String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			   Method getter = null;
			   try {
				   getter = this.getClass().getMethod(methodName);
			   } catch (NoSuchMethodException e) {
				   methodName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
				   getter = this.getClass().getMethod(methodName);
			   }
			   Object value = getter.invoke(this);
			   if (value != null) {
				   if ( value instanceof String ) {
						// Make sure to escape the '
						String text = ((String) value).replace("'", "''");
						values.add("'" + text + "'");
				   } else {
					   values.add(value);
				   }
			   } else {
				   values.add("NULL");
			   }
		   }
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Dto) {
			return ((Dto) o).id.equals(id);
		}
		return super.equals(o);
	}
	

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * @deprecated THIS IS STILL WORK IN PROGRESS AND NOT QUITE READY FOR PRIME TIME YET...
	 * @return
	 * @throws Exception
	 */
	public List<DtoFieldDetails> getDtoFieldDetails() throws Exception {
		List<DtoFieldDetails> list = new ArrayList<DtoFieldDetails>();
		
		// Start by the DTO base class
		for (Field f: Dto.class.getDeclaredFields()) {
			Column column = f.getAnnotation(Column.class);
			if (column != null) {
				String dbName = column.name();
 			    String fieldName = f.getName();
 			    String methodSuffix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
 			    
  			    String methodName = "get" + methodSuffix;
	 			Method getter = null;
	 			try {
	 			   getter = this.getClass().getMethod(methodName);
	 			} catch (NoSuchMethodException e) {
	 			   methodName = "is" + methodSuffix;
	 			   getter = this.getClass().getMethod(methodName);
	 		    }
				
	 			methodName = "set" + methodSuffix;	 			
	 			Method setter = this.getClass().getMethod(methodName, f.getType());

	 			DtoFieldDetails fieldDetails = new DtoFieldDetails(fieldName, dbName, getter, setter);
	 			list.add(fieldDetails);
			}
		}

		// Complete with the actual class
		for (Field f: this.getClass().getDeclaredFields()) {
			Column column = f.getAnnotation(Column.class);
			if (column != null) {
				String dbName = column.name();
 			    String fieldName = f.getName();
 			    String methodSuffix = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

 			    String methodName = "get" + methodSuffix;
	 			Method getter = null;
	 			try {
	 			   getter = this.getClass().getMethod(methodName);
	 			} catch (NoSuchMethodException e) {
	 			   methodName = "is" + methodSuffix;
	 			   getter = this.getClass().getMethod(methodName);
	 		    }
				
	 			methodName = "set" + methodSuffix;	 			
	 			Method setter = this.getClass().getMethod(methodName, f.getType());

	 			DtoFieldDetails fieldDetails = new DtoFieldDetails(fieldName, dbName, getter, setter);
	 			list.add(fieldDetails);
			}
		}
		
		return list;
	}
}
