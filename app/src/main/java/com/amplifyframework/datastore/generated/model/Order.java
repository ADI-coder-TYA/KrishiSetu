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

/** This is an auto generated class representing the Order type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Orders", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "buyerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.READ, ModelOperation.UPDATE }),
  @AuthRule(allow = AuthStrategy.PRIVATE, provider = "userPools", operations = { ModelOperation.READ, ModelOperation.UPDATE })
})
@Index(name = "byBuyerOrders", fields = {"buyerID","createdAt"})
@Index(name = "byFarmerOrders", fields = {"farmerID","createdAt"})
@Index(name = "byCropOrders", fields = {"cropID"})
public final class Order implements Model {
  public static final QueryField ID = field("Order", "id");
  public static final QueryField BUYER = field("Order", "buyerID");
  public static final QueryField FARMER = field("Order", "farmerID");
  public static final QueryField CROP = field("Order", "cropID");
  public static final QueryField QUANTITY = field("Order", "quantity");
  public static final QueryField BARGAINED_PRICE = field("Order", "bargainedPrice");
  public static final QueryField REAL_PRICE = field("Order", "realPrice");
  public static final QueryField DELIVERY_ADDRESS = field("Order", "deliveryAddress");
  public static final QueryField DELIVERY_PHONE = field("Order", "deliveryPhone");
  public static final QueryField DELIVERY_PINCODE = field("Order", "deliveryPincode");
  public static final QueryField ORDER_STATUS = field("Order", "orderStatus");
  public static final QueryField CREATED_AT = field("Order", "createdAt");
  public static final QueryField UPDATED_AT = field("Order", "updatedAt");
  public static final QueryField EXPIRES_AT = field("Order", "expiresAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "buyerID", targetNames = {"buyerID"}, type = User.class) User buyer;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "farmerID", targetNames = {"farmerID"}, type = User.class) User farmer;
  private final @ModelField(targetType="Crop") @BelongsTo(targetName = "cropID", targetNames = {"cropID"}, type = Crop.class) Crop crop;
  private final @ModelField(targetType="Int", isRequired = true) Integer quantity;
  private final @ModelField(targetType="Int", isRequired = true) Integer bargainedPrice;
  private final @ModelField(targetType="Int", isRequired = true) Integer realPrice;
  private final @ModelField(targetType="String", isRequired = true) String deliveryAddress;
  private final @ModelField(targetType="AWSPhone", isRequired = true) String deliveryPhone;
  private final @ModelField(targetType="String", isRequired = true) String deliveryPincode;
  private final @ModelField(targetType="OrderStatus", isRequired = true) OrderStatus orderStatus;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime createdAt;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime updatedAt;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime expiresAt;
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
  
  public Integer getQuantity() {
      return quantity;
  }
  
  public Integer getBargainedPrice() {
      return bargainedPrice;
  }
  
  public Integer getRealPrice() {
      return realPrice;
  }
  
  public String getDeliveryAddress() {
      return deliveryAddress;
  }
  
  public String getDeliveryPhone() {
      return deliveryPhone;
  }
  
  public String getDeliveryPincode() {
      return deliveryPincode;
  }
  
  public OrderStatus getOrderStatus() {
      return orderStatus;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public Temporal.DateTime getExpiresAt() {
      return expiresAt;
  }
  
  private Order(String id, User buyer, User farmer, Crop crop, Integer quantity, Integer bargainedPrice, Integer realPrice, String deliveryAddress, String deliveryPhone, String deliveryPincode, OrderStatus orderStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.DateTime expiresAt) {
    this.id = id;
    this.buyer = buyer;
    this.farmer = farmer;
    this.crop = crop;
    this.quantity = quantity;
    this.bargainedPrice = bargainedPrice;
    this.realPrice = realPrice;
    this.deliveryAddress = deliveryAddress;
    this.deliveryPhone = deliveryPhone;
    this.deliveryPincode = deliveryPincode;
    this.orderStatus = orderStatus;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.expiresAt = expiresAt;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Order order = (Order) obj;
      return ObjectsCompat.equals(getId(), order.getId()) &&
              ObjectsCompat.equals(getBuyer(), order.getBuyer()) &&
              ObjectsCompat.equals(getFarmer(), order.getFarmer()) &&
              ObjectsCompat.equals(getCrop(), order.getCrop()) &&
              ObjectsCompat.equals(getQuantity(), order.getQuantity()) &&
              ObjectsCompat.equals(getBargainedPrice(), order.getBargainedPrice()) &&
              ObjectsCompat.equals(getRealPrice(), order.getRealPrice()) &&
              ObjectsCompat.equals(getDeliveryAddress(), order.getDeliveryAddress()) &&
              ObjectsCompat.equals(getDeliveryPhone(), order.getDeliveryPhone()) &&
              ObjectsCompat.equals(getDeliveryPincode(), order.getDeliveryPincode()) &&
              ObjectsCompat.equals(getOrderStatus(), order.getOrderStatus()) &&
              ObjectsCompat.equals(getCreatedAt(), order.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), order.getUpdatedAt()) &&
              ObjectsCompat.equals(getExpiresAt(), order.getExpiresAt());
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
      .append(getBargainedPrice())
      .append(getRealPrice())
      .append(getDeliveryAddress())
      .append(getDeliveryPhone())
      .append(getDeliveryPincode())
      .append(getOrderStatus())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getExpiresAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Order {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("buyer=" + String.valueOf(getBuyer()) + ", ")
      .append("farmer=" + String.valueOf(getFarmer()) + ", ")
      .append("crop=" + String.valueOf(getCrop()) + ", ")
      .append("quantity=" + String.valueOf(getQuantity()) + ", ")
      .append("bargainedPrice=" + String.valueOf(getBargainedPrice()) + ", ")
      .append("realPrice=" + String.valueOf(getRealPrice()) + ", ")
      .append("deliveryAddress=" + String.valueOf(getDeliveryAddress()) + ", ")
      .append("deliveryPhone=" + String.valueOf(getDeliveryPhone()) + ", ")
      .append("deliveryPincode=" + String.valueOf(getDeliveryPincode()) + ", ")
      .append("orderStatus=" + String.valueOf(getOrderStatus()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("expiresAt=" + String.valueOf(getExpiresAt()))
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
  public static Order justId(String id) {
    return new Order(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
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
      bargainedPrice,
      realPrice,
      deliveryAddress,
      deliveryPhone,
      deliveryPincode,
      orderStatus,
      createdAt,
      updatedAt,
      expiresAt);
  }
  public interface QuantityStep {
    BargainedPriceStep quantity(Integer quantity);
  }
  

  public interface BargainedPriceStep {
    RealPriceStep bargainedPrice(Integer bargainedPrice);
  }
  

  public interface RealPriceStep {
    DeliveryAddressStep realPrice(Integer realPrice);
  }
  

  public interface DeliveryAddressStep {
    DeliveryPhoneStep deliveryAddress(String deliveryAddress);
  }
  

  public interface DeliveryPhoneStep {
    DeliveryPincodeStep deliveryPhone(String deliveryPhone);
  }
  

  public interface DeliveryPincodeStep {
    OrderStatusStep deliveryPincode(String deliveryPincode);
  }
  

  public interface OrderStatusStep {
    CreatedAtStep orderStatus(OrderStatus orderStatus);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    ExpiresAtStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface ExpiresAtStep {
    BuildStep expiresAt(Temporal.DateTime expiresAt);
  }
  

  public interface BuildStep {
    Order build();
    BuildStep id(String id);
    BuildStep buyer(User buyer);
    BuildStep farmer(User farmer);
    BuildStep crop(Crop crop);
  }
  

  public static class Builder implements QuantityStep, BargainedPriceStep, RealPriceStep, DeliveryAddressStep, DeliveryPhoneStep, DeliveryPincodeStep, OrderStatusStep, CreatedAtStep, UpdatedAtStep, ExpiresAtStep, BuildStep {
    private String id;
    private Integer quantity;
    private Integer bargainedPrice;
    private Integer realPrice;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryPincode;
    private OrderStatus orderStatus;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private Temporal.DateTime expiresAt;
    private User buyer;
    private User farmer;
    private Crop crop;
    public Builder() {
      
    }
    
    private Builder(String id, User buyer, User farmer, Crop crop, Integer quantity, Integer bargainedPrice, Integer realPrice, String deliveryAddress, String deliveryPhone, String deliveryPincode, OrderStatus orderStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.DateTime expiresAt) {
      this.id = id;
      this.buyer = buyer;
      this.farmer = farmer;
      this.crop = crop;
      this.quantity = quantity;
      this.bargainedPrice = bargainedPrice;
      this.realPrice = realPrice;
      this.deliveryAddress = deliveryAddress;
      this.deliveryPhone = deliveryPhone;
      this.deliveryPincode = deliveryPincode;
      this.orderStatus = orderStatus;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
      this.expiresAt = expiresAt;
    }
    
    @Override
     public Order build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Order(
          id,
          buyer,
          farmer,
          crop,
          quantity,
          bargainedPrice,
          realPrice,
          deliveryAddress,
          deliveryPhone,
          deliveryPincode,
          orderStatus,
          createdAt,
          updatedAt,
          expiresAt);
    }
    
    @Override
     public BargainedPriceStep quantity(Integer quantity) {
        Objects.requireNonNull(quantity);
        this.quantity = quantity;
        return this;
    }
    
    @Override
     public RealPriceStep bargainedPrice(Integer bargainedPrice) {
        Objects.requireNonNull(bargainedPrice);
        this.bargainedPrice = bargainedPrice;
        return this;
    }
    
    @Override
     public DeliveryAddressStep realPrice(Integer realPrice) {
        Objects.requireNonNull(realPrice);
        this.realPrice = realPrice;
        return this;
    }
    
    @Override
     public DeliveryPhoneStep deliveryAddress(String deliveryAddress) {
        Objects.requireNonNull(deliveryAddress);
        this.deliveryAddress = deliveryAddress;
        return this;
    }
    
    @Override
     public DeliveryPincodeStep deliveryPhone(String deliveryPhone) {
        Objects.requireNonNull(deliveryPhone);
        this.deliveryPhone = deliveryPhone;
        return this;
    }
    
    @Override
     public OrderStatusStep deliveryPincode(String deliveryPincode) {
        Objects.requireNonNull(deliveryPincode);
        this.deliveryPincode = deliveryPincode;
        return this;
    }
    
    @Override
     public CreatedAtStep orderStatus(OrderStatus orderStatus) {
        Objects.requireNonNull(orderStatus);
        this.orderStatus = orderStatus;
        return this;
    }
    
    @Override
     public UpdatedAtStep createdAt(Temporal.DateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
        return this;
    }
    
    @Override
     public ExpiresAtStep updatedAt(Temporal.DateTime updatedAt) {
        Objects.requireNonNull(updatedAt);
        this.updatedAt = updatedAt;
        return this;
    }
    
    @Override
     public BuildStep expiresAt(Temporal.DateTime expiresAt) {
        Objects.requireNonNull(expiresAt);
        this.expiresAt = expiresAt;
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
    private CopyOfBuilder(String id, User buyer, User farmer, Crop crop, Integer quantity, Integer bargainedPrice, Integer realPrice, String deliveryAddress, String deliveryPhone, String deliveryPincode, OrderStatus orderStatus, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.DateTime expiresAt) {
      super(id, buyer, farmer, crop, quantity, bargainedPrice, realPrice, deliveryAddress, deliveryPhone, deliveryPincode, orderStatus, createdAt, updatedAt, expiresAt);
      Objects.requireNonNull(quantity);
      Objects.requireNonNull(bargainedPrice);
      Objects.requireNonNull(realPrice);
      Objects.requireNonNull(deliveryAddress);
      Objects.requireNonNull(deliveryPhone);
      Objects.requireNonNull(deliveryPincode);
      Objects.requireNonNull(orderStatus);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
      Objects.requireNonNull(expiresAt);
    }
    
    @Override
     public CopyOfBuilder quantity(Integer quantity) {
      return (CopyOfBuilder) super.quantity(quantity);
    }
    
    @Override
     public CopyOfBuilder bargainedPrice(Integer bargainedPrice) {
      return (CopyOfBuilder) super.bargainedPrice(bargainedPrice);
    }
    
    @Override
     public CopyOfBuilder realPrice(Integer realPrice) {
      return (CopyOfBuilder) super.realPrice(realPrice);
    }
    
    @Override
     public CopyOfBuilder deliveryAddress(String deliveryAddress) {
      return (CopyOfBuilder) super.deliveryAddress(deliveryAddress);
    }
    
    @Override
     public CopyOfBuilder deliveryPhone(String deliveryPhone) {
      return (CopyOfBuilder) super.deliveryPhone(deliveryPhone);
    }
    
    @Override
     public CopyOfBuilder deliveryPincode(String deliveryPincode) {
      return (CopyOfBuilder) super.deliveryPincode(deliveryPincode);
    }
    
    @Override
     public CopyOfBuilder orderStatus(OrderStatus orderStatus) {
      return (CopyOfBuilder) super.orderStatus(orderStatus);
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
     public CopyOfBuilder expiresAt(Temporal.DateTime expiresAt) {
      return (CopyOfBuilder) super.expiresAt(expiresAt);
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
  

  public static class OrderIdentifier extends ModelIdentifier<Order> {
    private static final long serialVersionUID = 1L;
    public OrderIdentifier(String id) {
      super(id);
    }
  }
  
}
