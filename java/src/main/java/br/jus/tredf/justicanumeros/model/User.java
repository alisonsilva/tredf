/**
 * 
 */
package br.jus.tredf.justicanumeros.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="User")
public class User implements Serializable {
  private static final long serialVersionUID = 394143208771373474L;

  public static final String MSG_INVALID_USER = "user.newuser.invaliduser";
  public static final String MSG_USER_ALREADY_EXISTS = 
      "user.newuser.alreadyexists";
  public static final String MSG_USER_INSERTED_SUCESS = "user.newuser.success";
  public static final String MSG_USER_INVALID_ID = "user.remove.invalidid";
  public static final String MSG_USER_REMOVED_SUCCESS = "user.remove.success";
  public static final String MSG_USER_AUTHENTICATED = "user.authenticate";
  public static final String MSG_USER_ID_EMPTY = "user.useridisempty";
  
  @Id
  private String id;
  
  @Field(value="Username")
  private String userName;
  
  @Field(value="Password")
  private String password;
  
  public User(){}
  
  public User(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }
  
  public User(String id, String userName, String password) {
    this(userName, password);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id.hashCode() ^ (id.hashCode() >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    User other = (User) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() { 
    return "User [id=" + id + ", Username=" + userName + "]"; 
  }    
}
