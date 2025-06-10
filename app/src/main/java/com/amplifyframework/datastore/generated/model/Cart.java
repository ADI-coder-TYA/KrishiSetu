package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.annotations.HasMany;
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

/** This is an auto generated class representing the Cart type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Carts", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "buyerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.READ, ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE })
})
@Index(name = "cartByBuyer", fields = {"buyerID"})
public final class Cart implements Model {
  public static final QueryField ID = field("Cart", "id");
  public static final QueryField BUYER = field("Cart", "buyerID");
  public static final QueryField CREATED_AT = field("Cart", "createdAt");
  public static final QueryField UPDATED_AT = field("Cart", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "buyerID", targetNames = {"buyerID"}, type = User.class) User buyer;
  private final @ModelField(targetType="CartItem") @HasMany(associatedWith = "cart", type = CartItem.class) List<CartItem> items = null;
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
  
  public List<CartItem> getItems() {
      return items;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Cart(String id, User buyer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.buyer = buyer;
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
      Cart cart = (Cart) obj;
      return ObjectsCompat.equals(getId(), cart.getId()) &&
              ObjectsCompat.equals(getBuyer(), cart.getBuyer()) &&
              ObjectsCompat.equals(getCreatedAt(), cart.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), cart.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBuyer())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Cart {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("buyer=" + String.valueOf(getBuyer()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static CreatedAtStep builder() {
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
  public static Cart justId(String id) {
    return new Cart(
      id,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      buyer,
      createdAt,
      updatedAt);
  }
  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    Cart build();
    BuildStep id(String id);
    BuildStep buyer(User buyer);
  }
  

  public static class Builder implements CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private User buyer;
    public Builder() {
      
    }
    
    private Builder(String id, User buyer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.buyer = buyer;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public Cart build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Cart(
          id,
          buyer,
          createdAt,
          updatedAt);
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
    private CopyOfBuilder(String id, User buyer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, buyer, createdAt, updatedAt);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
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
  }
  

  public static class CartIdentifier extends ModelIdentifier<Cart> {
    private static final long serialVersionUID = 1L;
    public CartIdentifier(String id) {
      super(id);
    }
  }
  
}
