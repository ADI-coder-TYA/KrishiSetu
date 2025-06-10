package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
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

/** This is an auto generated class representing the Delivery type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Deliveries", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "agentID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.READ, ModelOperation.UPDATE }),
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "BuyerGroup", "FarmerGroup" }, provider = "userPools", operations = { ModelOperation.READ })
})
@Index(name = "deliveriesByPurchase", fields = {"purchaseID"})
@Index(name = "deliveriesByAgent", fields = {"agentID","createdAt"})
public final class Delivery implements Model {
  public static final QueryField ID = field("Delivery", "id");
  public static final QueryField PURCHASE = field("Delivery", "purchaseID");
  public static final QueryField AGENT = field("Delivery", "agentID");
  public static final QueryField DELIVERY_ADDRESS = field("Delivery", "deliveryAddress");
  public static final QueryField DELIVERY_STATUS = field("Delivery", "deliveryStatus");
  public static final QueryField CREATED_AT = field("Delivery", "createdAt");
  public static final QueryField UPDATED_AT = field("Delivery", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Purchase") @BelongsTo(targetName = "purchaseID", targetNames = {"purchaseID"}, type = Purchase.class) Purchase purchase;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "agentID", targetNames = {"agentID"}, type = User.class) User agent;
  private final @ModelField(targetType="String", isRequired = true) String deliveryAddress;
  private final @ModelField(targetType="DeliveryStatus", isRequired = true) DeliveryStatus deliveryStatus;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime createdAt;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public Purchase getPurchase() {
      return purchase;
  }
  
  public User getAgent() {
      return agent;
  }
  
  public String getDeliveryAddress() {
      return deliveryAddress;
  }
  
  public DeliveryStatus getDeliveryStatus() {
      return deliveryStatus;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Delivery(String id, Purchase purchase, User agent, String deliveryAddress, DeliveryStatus deliveryStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.purchase = purchase;
    this.agent = agent;
    this.deliveryAddress = deliveryAddress;
    this.deliveryStatus = deliveryStatus;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Delivery delivery = (Delivery) obj;
      return ObjectsCompat.equals(getId(), delivery.getId()) &&
              ObjectsCompat.equals(getPurchase(), delivery.getPurchase()) &&
              ObjectsCompat.equals(getAgent(), delivery.getAgent()) &&
              ObjectsCompat.equals(getDeliveryAddress(), delivery.getDeliveryAddress()) &&
              ObjectsCompat.equals(getDeliveryStatus(), delivery.getDeliveryStatus()) &&
              ObjectsCompat.equals(getCreatedAt(), delivery.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), delivery.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getPurchase())
      .append(getAgent())
      .append(getDeliveryAddress())
      .append(getDeliveryStatus())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Delivery {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("purchase=" + String.valueOf(getPurchase()) + ", ")
      .append("agent=" + String.valueOf(getAgent()) + ", ")
      .append("deliveryAddress=" + String.valueOf(getDeliveryAddress()) + ", ")
      .append("deliveryStatus=" + String.valueOf(getDeliveryStatus()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static DeliveryAddressStep builder() {
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
  public static Delivery justId(String id) {
    return new Delivery(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      purchase,
      agent,
      deliveryAddress,
      deliveryStatus,
      createdAt,
      updatedAt);
  }
  public interface DeliveryAddressStep {
    DeliveryStatusStep deliveryAddress(String deliveryAddress);
  }
  

  public interface DeliveryStatusStep {
    CreatedAtStep deliveryStatus(DeliveryStatus deliveryStatus);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    Delivery build();
    BuildStep id(String id);
    BuildStep purchase(Purchase purchase);
    BuildStep agent(User agent);
  }
  

  public static class Builder implements DeliveryAddressStep, DeliveryStatusStep, CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private String deliveryAddress;
    private DeliveryStatus deliveryStatus;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private Purchase purchase;
    private User agent;
    public Builder() {
      
    }
    
    private Builder(String id, Purchase purchase, User agent, String deliveryAddress, DeliveryStatus deliveryStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.purchase = purchase;
      this.agent = agent;
      this.deliveryAddress = deliveryAddress;
      this.deliveryStatus = deliveryStatus;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public Delivery build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Delivery(
          id,
          purchase,
          agent,
          deliveryAddress,
          deliveryStatus,
          createdAt,
          updatedAt);
    }
    
    @Override
     public DeliveryStatusStep deliveryAddress(String deliveryAddress) {
        Objects.requireNonNull(deliveryAddress);
        this.deliveryAddress = deliveryAddress;
        return this;
    }
    
    @Override
     public CreatedAtStep deliveryStatus(DeliveryStatus deliveryStatus) {
        Objects.requireNonNull(deliveryStatus);
        this.deliveryStatus = deliveryStatus;
        return this;
    }
    
    @Override
     public UpdatedAtStep createdAt(Temporal.DateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
        return this;
    }
    
    @Override
     public BuildStep updatedAt(Temporal.DateTime updatedAt) {
        Objects.requireNonNull(updatedAt);
        this.updatedAt = updatedAt;
        return this;
    }
    
    @Override
     public BuildStep purchase(Purchase purchase) {
        this.purchase = purchase;
        return this;
    }
    
    @Override
     public BuildStep agent(User agent) {
        this.agent = agent;
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
    private CopyOfBuilder(String id, Purchase purchase, User agent, String deliveryAddress, DeliveryStatus deliveryStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, purchase, agent, deliveryAddress, deliveryStatus, createdAt, updatedAt);
      Objects.requireNonNull(deliveryAddress);
      Objects.requireNonNull(deliveryStatus);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
    }
    
    @Override
     public CopyOfBuilder deliveryAddress(String deliveryAddress) {
      return (CopyOfBuilder) super.deliveryAddress(deliveryAddress);
    }
    
    @Override
     public CopyOfBuilder deliveryStatus(DeliveryStatus deliveryStatus) {
      return (CopyOfBuilder) super.deliveryStatus(deliveryStatus);
    }
    
    @Override
     public CopyOfBuilder createdAt(Temporal.DateTime createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder updatedAt(Temporal.DateTime updatedAt) {
      return (CopyOfBuilder) super.updatedAt(updatedAt);
    }
    
    @Override
     public CopyOfBuilder purchase(Purchase purchase) {
      return (CopyOfBuilder) super.purchase(purchase);
    }
    
    @Override
     public CopyOfBuilder agent(User agent) {
      return (CopyOfBuilder) super.agent(agent);
    }
  }
  

  public static class DeliveryIdentifier extends ModelIdentifier<Delivery> {
    private static final long serialVersionUID = 1L;
    public DeliveryIdentifier(String id) {
      super(id);
    }
  }
  
}
