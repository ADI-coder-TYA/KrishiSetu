package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
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

/** This is an auto generated class representing the Crop type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Crops", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "farmerID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.READ, ModelOperation.UPDATE, ModelOperation.DELETE }),
  @AuthRule(allow = AuthStrategy.GROUPS, groupClaim = "cognito:groups", groups = { "BuyerGroup", "DeliveryGroup", "FarmerGroup" }, provider = "userPools", operations = { ModelOperation.READ })
})
@Index(name = "byFarmer", fields = {"farmerID","createdAt"})
public final class Crop implements Model {
  public static final QueryField ID = field("Crop", "id");
  public static final QueryField TITLE = field("Crop", "title");
  public static final QueryField DESCRIPTION = field("Crop", "description");
  public static final QueryField PRICE = field("Crop", "price");
  public static final QueryField QUANTITY_AVAILABLE = field("Crop", "quantityAvailable");
  public static final QueryField IMAGE_URL = field("Crop", "imageUrl");
  public static final QueryField LOCATION = field("Crop", "location");
  public static final QueryField FARMER = field("Crop", "farmerID");
  public static final QueryField CREATED_AT = field("Crop", "createdAt");
  public static final QueryField UPDATED_AT = field("Crop", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="Float", isRequired = true) Double price;
  private final @ModelField(targetType="Int", isRequired = true) Integer quantityAvailable;
  private final @ModelField(targetType="String") String imageUrl;
  private final @ModelField(targetType="String") String location;
  private final @ModelField(targetType="CartItem") @HasMany(associatedWith = "crop", type = CartItem.class) List<CartItem> cartItems = null;
  private final @ModelField(targetType="Purchase") @HasMany(associatedWith = "crop", type = Purchase.class) List<Purchase> purchases = null;
  private final @ModelField(targetType="Order") @HasMany(associatedWith = "crop", type = Order.class) List<Order> orders = null;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "farmerID", targetNames = {"farmerID"}, type = User.class) User farmer;
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
  
  public String getTitle() {
      return title;
  }
  
  public String getDescription() {
      return description;
  }
  
  public Double getPrice() {
      return price;
  }
  
  public Integer getQuantityAvailable() {
      return quantityAvailable;
  }
  
  public String getImageUrl() {
      return imageUrl;
  }
  
  public String getLocation() {
      return location;
  }
  
  public List<CartItem> getCartItems() {
      return cartItems;
  }
  
  public List<Purchase> getPurchases() {
      return purchases;
  }
  
  public List<Order> getOrders() {
      return orders;
  }
  
  public User getFarmer() {
      return farmer;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Crop(String id, String title, String description, Double price, Integer quantityAvailable, String imageUrl, String location, User farmer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.price = price;
    this.quantityAvailable = quantityAvailable;
    this.imageUrl = imageUrl;
    this.location = location;
    this.farmer = farmer;
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
      Crop crop = (Crop) obj;
      return ObjectsCompat.equals(getId(), crop.getId()) &&
              ObjectsCompat.equals(getTitle(), crop.getTitle()) &&
              ObjectsCompat.equals(getDescription(), crop.getDescription()) &&
              ObjectsCompat.equals(getPrice(), crop.getPrice()) &&
              ObjectsCompat.equals(getQuantityAvailable(), crop.getQuantityAvailable()) &&
              ObjectsCompat.equals(getImageUrl(), crop.getImageUrl()) &&
              ObjectsCompat.equals(getLocation(), crop.getLocation()) &&
              ObjectsCompat.equals(getFarmer(), crop.getFarmer()) &&
              ObjectsCompat.equals(getCreatedAt(), crop.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), crop.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getDescription())
      .append(getPrice())
      .append(getQuantityAvailable())
      .append(getImageUrl())
      .append(getLocation())
      .append(getFarmer())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Crop {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("price=" + String.valueOf(getPrice()) + ", ")
      .append("quantityAvailable=" + String.valueOf(getQuantityAvailable()) + ", ")
      .append("imageUrl=" + String.valueOf(getImageUrl()) + ", ")
      .append("location=" + String.valueOf(getLocation()) + ", ")
      .append("farmer=" + String.valueOf(getFarmer()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
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
  public static Crop justId(String id) {
    return new Crop(
      id,
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
      title,
      description,
      price,
      quantityAvailable,
      imageUrl,
      location,
      farmer,
      createdAt,
      updatedAt);
  }
  public interface TitleStep {
    PriceStep title(String title);
  }
  

  public interface PriceStep {
    QuantityAvailableStep price(Double price);
  }
  

  public interface QuantityAvailableStep {
    CreatedAtStep quantityAvailable(Integer quantityAvailable);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    Crop build();
    BuildStep id(String id);
    BuildStep description(String description);
    BuildStep imageUrl(String imageUrl);
    BuildStep location(String location);
    BuildStep farmer(User farmer);
  }
  

  public static class Builder implements TitleStep, PriceStep, QuantityAvailableStep, CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private String title;
    private Double price;
    private Integer quantityAvailable;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private String description;
    private String imageUrl;
    private String location;
    private User farmer;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String description, Double price, Integer quantityAvailable, String imageUrl, String location, User farmer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.title = title;
      this.description = description;
      this.price = price;
      this.quantityAvailable = quantityAvailable;
      this.imageUrl = imageUrl;
      this.location = location;
      this.farmer = farmer;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public Crop build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Crop(
          id,
          title,
          description,
          price,
          quantityAvailable,
          imageUrl,
          location,
          farmer,
          createdAt,
          updatedAt);
    }
    
    @Override
     public PriceStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public QuantityAvailableStep price(Double price) {
        Objects.requireNonNull(price);
        this.price = price;
        return this;
    }
    
    @Override
     public CreatedAtStep quantityAvailable(Integer quantityAvailable) {
        Objects.requireNonNull(quantityAvailable);
        this.quantityAvailable = quantityAvailable;
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
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
    
    @Override
     public BuildStep location(String location) {
        this.location = location;
        return this;
    }
    
    @Override
     public BuildStep farmer(User farmer) {
        this.farmer = farmer;
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
    private CopyOfBuilder(String id, String title, String description, Double price, Integer quantityAvailable, String imageUrl, String location, User farmer, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, title, description, price, quantityAvailable, imageUrl, location, farmer, createdAt, updatedAt);
      Objects.requireNonNull(title);
      Objects.requireNonNull(price);
      Objects.requireNonNull(quantityAvailable);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder price(Double price) {
      return (CopyOfBuilder) super.price(price);
    }
    
    @Override
     public CopyOfBuilder quantityAvailable(Integer quantityAvailable) {
      return (CopyOfBuilder) super.quantityAvailable(quantityAvailable);
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
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder imageUrl(String imageUrl) {
      return (CopyOfBuilder) super.imageUrl(imageUrl);
    }
    
    @Override
     public CopyOfBuilder location(String location) {
      return (CopyOfBuilder) super.location(location);
    }
    
    @Override
     public CopyOfBuilder farmer(User farmer) {
      return (CopyOfBuilder) super.farmer(farmer);
    }
  }
  

  public static class CropIdentifier extends ModelIdentifier<Crop> {
    private static final long serialVersionUID = 1L;
    public CropIdentifier(String id) {
      super(id);
    }
  }
  
}
