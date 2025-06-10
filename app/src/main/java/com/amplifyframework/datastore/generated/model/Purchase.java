package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
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

/** This is an auto generated class representing the Purchase type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Purchases", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "buyerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.READ, ModelOperation.CREATE }),
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "FarmerGroup" }, provider = "userPools", operations = { ModelOperation.READ })
})
@Index(name = "byBuyer", fields = {"buyerID","createdAt"})
@Index(name = "byFarmer", fields = {"farmerID","createdAt"})
@Index(name = "byCropPurchase", fields = {"cropID"})
public final class Purchase implements Model {
  public static final QueryField ID = field("Purchase", "id");
  public static final QueryField BUYER = field("Purchase", "buyerID");
  public static final QueryField FARMER = field("Purchase", "farmerID");
  public static final QueryField CROP = field("Purchase", "cropID");
  public static final QueryField QUANTITY = field("Purchase", "quantity");
  public static final QueryField TOTAL_AMOUNT = field("Purchase", "totalAmount");
  public static final QueryField CREATED_AT = field("Purchase", "createdAt");
  public static final QueryField UPDATED_AT = field("Purchase", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "buyerID", targetNames = {"buyerID"}, type = User.class) User buyer;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "farmerID", targetNames = {"farmerID"}, type = User.class) User farmer;
  private final @ModelField(targetType="Crop") @BelongsTo(targetName = "cropID", targetNames = {"cropID"}, type = Crop.class) Crop crop;
  private final @ModelField(targetType="Delivery") @HasOne(associatedWith = "purchase", type = Delivery.class) Delivery delivery = null;
  private final @ModelField(targetType="Int", isRequired = true) Integer quantity;
  private final @ModelField(targetType="Float", isRequired = true) Double totalAmount;
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
  
  public User getBuyer() {
      return buyer;
  }
  
  public User getFarmer() {
      return farmer;
  }
  
  public Crop getCrop() {
      return crop;
  }
  
  public Delivery getDelivery() {
      return delivery;
  }
  
  public Integer getQuantity() {
      return quantity;
  }
  
  public Double getTotalAmount() {
      return totalAmount;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Purchase(String id, User buyer, User farmer, Crop crop, Integer quantity, Double totalAmount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.buyer = buyer;
    this.farmer = farmer;
    this.crop = crop;
    this.quantity = quantity;
    this.totalAmount = totalAmount;
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
      Purchase purchase = (Purchase) obj;
      return ObjectsCompat.equals(getId(), purchase.getId()) &&
              ObjectsCompat.equals(getBuyer(), purchase.getBuyer()) &&
              ObjectsCompat.equals(getFarmer(), purchase.getFarmer()) &&
              ObjectsCompat.equals(getCrop(), purchase.getCrop()) &&
              ObjectsCompat.equals(getQuantity(), purchase.getQuantity()) &&
              ObjectsCompat.equals(getTotalAmount(), purchase.getTotalAmount()) &&
              ObjectsCompat.equals(getCreatedAt(), purchase.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), purchase.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBuyer())
      .append(getFarmer())
      .append(getCrop())
      .append(getQuantity())
      .append(getTotalAmount())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Purchase {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("buyer=" + String.valueOf(getBuyer()) + ", ")
      .append("farmer=" + String.valueOf(getFarmer()) + ", ")
      .append("crop=" + String.valueOf(getCrop()) + ", ")
      .append("quantity=" + String.valueOf(getQuantity()) + ", ")
      .append("totalAmount=" + String.valueOf(getTotalAmount()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static QuantityStep builder() {
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
  public static Purchase justId(String id) {
    return new Purchase(
      id,
      null,
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
      buyer,
      farmer,
      crop,
      quantity,
      totalAmount,
      createdAt,
      updatedAt);
  }
  public interface QuantityStep {
    TotalAmountStep quantity(Integer quantity);
  }
  

  public interface TotalAmountStep {
    CreatedAtStep totalAmount(Double totalAmount);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    Purchase build();
    BuildStep id(String id);
    BuildStep buyer(User buyer);
    BuildStep farmer(User farmer);
    BuildStep crop(Crop crop);
  }
  

  public static class Builder implements QuantityStep, TotalAmountStep, CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private Integer quantity;
    private Double totalAmount;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private User buyer;
    private User farmer;
    private Crop crop;
    public Builder() {
      
    }
    
    private Builder(String id, User buyer, User farmer, Crop crop, Integer quantity, Double totalAmount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.buyer = buyer;
      this.farmer = farmer;
      this.crop = crop;
      this.quantity = quantity;
      this.totalAmount = totalAmount;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public Purchase build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Purchase(
          id,
          buyer,
          farmer,
          crop,
          quantity,
          totalAmount,
          createdAt,
          updatedAt);
    }
    
    @Override
     public TotalAmountStep quantity(Integer quantity) {
        Objects.requireNonNull(quantity);
        this.quantity = quantity;
        return this;
    }
    
    @Override
     public CreatedAtStep totalAmount(Double totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
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
     public BuildStep buyer(User buyer) {
        this.buyer = buyer;
        return this;
    }
    
    @Override
     public BuildStep farmer(User farmer) {
        this.farmer = farmer;
        return this;
    }
    
    @Override
     public BuildStep crop(Crop crop) {
        this.crop = crop;
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
    private CopyOfBuilder(String id, User buyer, User farmer, Crop crop, Integer quantity, Double totalAmount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, buyer, farmer, crop, quantity, totalAmount, createdAt, updatedAt);
      Objects.requireNonNull(quantity);
      Objects.requireNonNull(totalAmount);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
    }
    
    @Override
     public CopyOfBuilder quantity(Integer quantity) {
      return (CopyOfBuilder) super.quantity(quantity);
    }
    
    @Override
     public CopyOfBuilder totalAmount(Double totalAmount) {
      return (CopyOfBuilder) super.totalAmount(totalAmount);
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
     public CopyOfBuilder buyer(User buyer) {
      return (CopyOfBuilder) super.buyer(buyer);
    }
    
    @Override
     public CopyOfBuilder farmer(User farmer) {
      return (CopyOfBuilder) super.farmer(farmer);
    }
    
    @Override
     public CopyOfBuilder crop(Crop crop) {
      return (CopyOfBuilder) super.crop(crop);
    }
  }
  

  public static class PurchaseIdentifier extends ModelIdentifier<Purchase> {
    private static final long serialVersionUID = 1L;
    public PurchaseIdentifier(String id) {
      super(id);
    }
  }
  
}
