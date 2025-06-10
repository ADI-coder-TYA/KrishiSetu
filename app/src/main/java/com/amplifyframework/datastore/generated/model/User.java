package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
import com.amplifyframework.core.model.annotations.HasOne;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PRIVATE, provider = "userPools", operations = { ModelOperation.CREATE }),
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "ownerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.READ, ModelOperation.UPDATE, ModelOperation.DELETE }),
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "FarmerGroup", "BuyerGroup", "DeliveryGroup" }, provider = "userPools", operations = { ModelOperation.READ })
})
@Index(name = "byRole", fields = {"role"})
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField NAME = field("User", "name");
  public static final QueryField EMAIL = field("User", "email");
  public static final QueryField PHONE = field("User", "phone");
  public static final QueryField OWNER_ID = field("User", "ownerID");
  public static final QueryField ROLE = field("User", "role");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="AWSEmail", isRequired = true) String email;
  private final @ModelField(targetType="AWSPhone") String phone;
  private final @ModelField(targetType="ID", isRequired = true) String ownerID;
  private final @ModelField(targetType="UserRole", isRequired = true) UserRole role;
  private final @ModelField(targetType="Crop") @HasMany(associatedWith = "farmer", type = Crop.class) List<Crop> crops = null;
  private final @ModelField(targetType="Purchase") @HasMany(associatedWith = "buyer", type = Purchase.class) List<Purchase> purchases = null;
  private final @ModelField(targetType="Cart") @HasOne(associatedWith = "buyer", type = Cart.class) Cart cart = null;
  private final @ModelField(targetType="Delivery") @HasMany(associatedWith = "agent", type = Delivery.class) List<Delivery> deliveriesAssigned = null;
  private final @ModelField(targetType="Message") @HasMany(associatedWith = "sender", type = Message.class) List<Message> sentMessages = null;
  private final @ModelField(targetType="Message") @HasMany(associatedWith = "receiver", type = Message.class) List<Message> receivedMessages = null;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getOwnerId() {
      return ownerID;
  }
  
  public UserRole getRole() {
      return role;
  }
  
  public List<Crop> getCrops() {
      return crops;
  }
  
  public List<Purchase> getPurchases() {
      return purchases;
  }
  
  public Cart getCart() {
      return cart;
  }
  
  public List<Delivery> getDeliveriesAssigned() {
      return deliveriesAssigned;
  }
  
  public List<Message> getSentMessages() {
      return sentMessages;
  }
  
  public List<Message> getReceivedMessages() {
      return receivedMessages;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User(String id, String name, String email, String phone, String ownerID, UserRole role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.ownerID = ownerID;
    this.role = role;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getName(), user.getName()) &&
              ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getPhone(), user.getPhone()) &&
              ObjectsCompat.equals(getOwnerId(), user.getOwnerId()) &&
              ObjectsCompat.equals(getRole(), user.getRole()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getEmail())
      .append(getPhone())
      .append(getOwnerId())
      .append(getRole())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("ownerID=" + String.valueOf(getOwnerId()) + ", ")
      .append("role=" + String.valueOf(getRole()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static User justId(String id) {
    return new User(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      email,
      phone,
      ownerID,
      role);
  }
  public interface NameStep {
    EmailStep name(String name);
  }
  

  public interface EmailStep {
    OwnerIdStep email(String email);
  }
  

  public interface OwnerIdStep {
    RoleStep ownerId(String ownerId);
  }
  

  public interface RoleStep {
    BuildStep role(UserRole role);
  }
  

  public interface BuildStep {
    User build();
    BuildStep id(String id);
    BuildStep phone(String phone);
  }
  

  public static class Builder implements NameStep, EmailStep, OwnerIdStep, RoleStep, BuildStep {
    private String id;
    private String name;
    private String email;
    private String ownerID;
    private UserRole role;
    private String phone;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String email, String phone, String ownerID, UserRole role) {
      this.id = id;
      this.name = name;
      this.email = email;
      this.phone = phone;
      this.ownerID = ownerID;
      this.role = role;
    }
    
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          name,
          email,
          phone,
          ownerID,
          role);
    }
    
    @Override
     public EmailStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public OwnerIdStep email(String email) {
        Objects.requireNonNull(email);
        this.email = email;
        return this;
    }
    
    @Override
     public RoleStep ownerId(String ownerId) {
        Objects.requireNonNull(ownerId);
        this.ownerID = ownerId;
        return this;
    }
    
    @Override
     public BuildStep role(UserRole role) {
        Objects.requireNonNull(role);
        this.role = role;
        return this;
    }
    
    @Override
     public BuildStep phone(String phone) {
        this.phone = phone;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String email, String phone, String ownerId, UserRole role) {
      super(id, name, email, phone, ownerID, role);
      Objects.requireNonNull(name);
      Objects.requireNonNull(email);
      Objects.requireNonNull(ownerID);
      Objects.requireNonNull(role);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder ownerId(String ownerId) {
      return (CopyOfBuilder) super.ownerId(ownerId);
    }
    
    @Override
     public CopyOfBuilder role(UserRole role) {
      return (CopyOfBuilder) super.role(role);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
    }
  }
  

  public static class UserIdentifier extends ModelIdentifier<User> {
    private static final long serialVersionUID = 1L;
    public UserIdentifier(String id) {
      super(id);
    }
  }
  
}
