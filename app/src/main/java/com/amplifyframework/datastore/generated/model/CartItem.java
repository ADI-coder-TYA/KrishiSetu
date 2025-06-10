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

/** This is an auto generated class representing the CartItem type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "CartItems", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "buyerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.READ, ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE })
})
@Index(name = "byCart", fields = {"cartID","buyerID","addedAt"})
@Index(name = "byCrop", fields = {"cropID"})
public final class CartItem implements Model {
  public static final QueryField ID = field("CartItem", "id");
  public static final QueryField BUYER_ID = field("CartItem", "buyerID");
  public static final QueryField CART = field("CartItem", "cartID");
  public static final QueryField CROP = field("CartItem", "cropID");
  public static final QueryField QUANTITY = field("CartItem", "quantity");
  public static final QueryField PRICE_AT_ADD = field("CartItem", "priceAtAdd");
  public static final QueryField ADDED_AT = field("CartItem", "addedAt");
  public static final QueryField CREATED_AT = field("CartItem", "createdAt");
  public static final QueryField UPDATED_AT = field("CartItem", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="ID", isRequired = true) String buyerID;
  private final @ModelField(targetType="Cart") @BelongsTo(targetName = "cartID", targetNames = {"cartID"}, type = Cart.class) Cart cart;
  private final @ModelField(targetType="Crop") @BelongsTo(targetName = "cropID", targetNames = {"cropID"}, type = Crop.class) Crop crop;
  private final @ModelField(targetType="Int", isRequired = true) Integer quantity;
  private final @ModelField(targetType="Float", isRequired = true) Double priceAtAdd;
  private final @ModelField(targetType="AWSDateTime", isRequired = true) Temporal.DateTime addedAt;
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
  
  public String getBuyerId() {
      return buyerID;
  }
  
  public Cart getCart() {
      return cart;
  }
  
  public Crop getCrop() {
      return crop;
  }
  
  public Integer getQuantity() {
      return quantity;
  }
  
  public Double getPriceAtAdd() {
      return priceAtAdd;
  }
  
  public Temporal.DateTime getAddedAt() {
      return addedAt;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private CartItem(String id, String buyerID, Cart cart, Crop crop, Integer quantity, Double priceAtAdd, Temporal.DateTime addedAt, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.buyerID = buyerID;
    this.cart = cart;
    this.crop = crop;
    this.quantity = quantity;
    this.priceAtAdd = priceAtAdd;
    this.addedAt = addedAt;
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
      CartItem cartItem = (CartItem) obj;
      return ObjectsCompat.equals(getId(), cartItem.getId()) &&
              ObjectsCompat.equals(getBuyerId(), cartItem.getBuyerId()) &&
              ObjectsCompat.equals(getCart(), cartItem.getCart()) &&
              ObjectsCompat.equals(getCrop(), cartItem.getCrop()) &&
              ObjectsCompat.equals(getQuantity(), cartItem.getQuantity()) &&
              ObjectsCompat.equals(getPriceAtAdd(), cartItem.getPriceAtAdd()) &&
              ObjectsCompat.equals(getAddedAt(), cartItem.getAddedAt()) &&
              ObjectsCompat.equals(getCreatedAt(), cartItem.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), cartItem.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBuyerId())
      .append(getCart())
      .append(getCrop())
      .append(getQuantity())
      .append(getPriceAtAdd())
      .append(getAddedAt())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("CartItem {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("buyerID=" + String.valueOf(getBuyerId()) + ", ")
      .append("cart=" + String.valueOf(getCart()) + ", ")
      .append("crop=" + String.valueOf(getCrop()) + ", ")
      .append("quantity=" + String.valueOf(getQuantity()) + ", ")
      .append("priceAtAdd=" + String.valueOf(getPriceAtAdd()) + ", ")
      .append("addedAt=" + String.valueOf(getAddedAt()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuyerIdStep builder() {
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
  public static CartItem justId(String id) {
    return new CartItem(
      id,
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
      buyerID,
      cart,
      crop,
      quantity,
      priceAtAdd,
      addedAt,
      createdAt,
      updatedAt);
  }
  public interface BuyerIdStep {
    QuantityStep buyerId(String buyerId);
  }
  

  public interface QuantityStep {
    PriceAtAddStep quantity(Integer quantity);
  }
  

  public interface PriceAtAddStep {
    AddedAtStep priceAtAdd(Double priceAtAdd);
  }
  

  public interface AddedAtStep {
    CreatedAtStep addedAt(Temporal.DateTime addedAt);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    CartItem build();
    BuildStep id(String id);
    BuildStep cart(Cart cart);
    BuildStep crop(Crop crop);
  }
  

  public static class Builder implements BuyerIdStep, QuantityStep, PriceAtAddStep, AddedAtStep, CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private String buyerID;
    private Integer quantity;
    private Double priceAtAdd;
    private Temporal.DateTime addedAt;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private Cart cart;
    private Crop crop;
    public Builder() {
      
    }
    
    private Builder(String id, String buyerID, Cart cart, Crop crop, Integer quantity, Double priceAtAdd, Temporal.DateTime addedAt, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.buyerID = buyerID;
      this.cart = cart;
      this.crop = crop;
      this.quantity = quantity;
      this.priceAtAdd = priceAtAdd;
      this.addedAt = addedAt;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public CartItem build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new CartItem(
          id,
          buyerID,
          cart,
          crop,
          quantity,
          priceAtAdd,
          addedAt,
          createdAt,
          updatedAt);
    }
    
    @Override
     public QuantityStep buyerId(String buyerId) {
        Objects.requireNonNull(buyerId);
        this.buyerID = buyerId;
        return this;
    }
    
    @Override
     public PriceAtAddStep quantity(Integer quantity) {
        Objects.requireNonNull(quantity);
        this.quantity = quantity;
        return this;
    }
    
    @Override
     public AddedAtStep priceAtAdd(Double priceAtAdd) {
        Objects.requireNonNull(priceAtAdd);
        this.priceAtAdd = priceAtAdd;
        return this;
    }
    
    @Override
     public CreatedAtStep addedAt(Temporal.DateTime addedAt) {
        Objects.requireNonNull(addedAt);
        this.addedAt = addedAt;
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
     public BuildStep cart(Cart cart) {
        this.cart = cart;
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
    private CopyOfBuilder(String id, String buyerId, Cart cart, Crop crop, Integer quantity, Double priceAtAdd, Temporal.DateTime addedAt, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, buyerID, cart, crop, quantity, priceAtAdd, addedAt, createdAt, updatedAt);
      Objects.requireNonNull(buyerID);
      Objects.requireNonNull(quantity);
      Objects.requireNonNull(priceAtAdd);
      Objects.requireNonNull(addedAt);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
    }
    
    @Override
     public CopyOfBuilder buyerId(String buyerId) {
      return (CopyOfBuilder) super.buyerId(buyerId);
    }
    
    @Override
     public CopyOfBuilder quantity(Integer quantity) {
      return (CopyOfBuilder) super.quantity(quantity);
    }
    
    @Override
     public CopyOfBuilder priceAtAdd(Double priceAtAdd) {
      return (CopyOfBuilder) super.priceAtAdd(priceAtAdd);
    }
    
    @Override
     public CopyOfBuilder addedAt(Temporal.DateTime addedAt) {
      return (CopyOfBuilder) super.addedAt(addedAt);
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
     public CopyOfBuilder cart(Cart cart) {
      return (CopyOfBuilder) super.cart(cart);
    }
    
    @Override
     public CopyOfBuilder crop(Crop crop) {
      return (CopyOfBuilder) super.crop(crop);
    }
  }
  

  public static class CartItemIdentifier extends ModelIdentifier<CartItem> {
    private static final long serialVersionUID = 1L;
    public CartItemIdentifier(String id) {
      super(id);
    }
  }
  
}
