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

/** This is an auto generated class representing the Message type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Messages", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "senderID", identityClaim = "sub", provider = "userPools", operations = { ModelOperation.CREATE, ModelOperation.READ, ModelOperation.UPDATE, ModelOperation.DELETE }),
  @AuthRule(allow = AuthStrategy.PRIVATE, provider = "userPools", operations = { ModelOperation.READ })
})
@Index(name = "messagesBySender", fields = {"senderID","createdAt"})
@Index(name = "messagesByReceiver", fields = {"receiverID","createdAt"})
public final class Message implements Model {
  public static final QueryField ID = field("Message", "id");
  public static final QueryField SENDER = field("Message", "senderID");
  public static final QueryField RECEIVER = field("Message", "receiverID");
  public static final QueryField CONTENT = field("Message", "content");
  public static final QueryField IS_READ = field("Message", "isRead");
  public static final QueryField CREATED_AT = field("Message", "createdAt");
  public static final QueryField UPDATED_AT = field("Message", "updatedAt");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "senderID", targetNames = {"senderID"}, type = User.class) User sender;
  private final @ModelField(targetType="User") @BelongsTo(targetName = "receiverID", targetNames = {"receiverID"}, type = User.class) User receiver;
  private final @ModelField(targetType="String", isRequired = true) String content;
  private final @ModelField(targetType="Boolean", isRequired = true) Boolean isRead;
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
  
  public User getSender() {
      return sender;
  }
  
  public User getReceiver() {
      return receiver;
  }
  
  public String getContent() {
      return content;
  }
  
  public Boolean getIsRead() {
      return isRead;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Message(String id, User sender, User receiver, String content, Boolean isRead, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
    this.id = id;
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
    this.isRead = isRead;
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
      Message message = (Message) obj;
      return ObjectsCompat.equals(getId(), message.getId()) &&
              ObjectsCompat.equals(getSender(), message.getSender()) &&
              ObjectsCompat.equals(getReceiver(), message.getReceiver()) &&
              ObjectsCompat.equals(getContent(), message.getContent()) &&
              ObjectsCompat.equals(getIsRead(), message.getIsRead()) &&
              ObjectsCompat.equals(getCreatedAt(), message.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), message.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getSender())
      .append(getReceiver())
      .append(getContent())
      .append(getIsRead())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Message {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("sender=" + String.valueOf(getSender()) + ", ")
      .append("receiver=" + String.valueOf(getReceiver()) + ", ")
      .append("content=" + String.valueOf(getContent()) + ", ")
      .append("isRead=" + String.valueOf(getIsRead()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static ContentStep builder() {
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
  public static Message justId(String id) {
    return new Message(
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
      sender,
      receiver,
      content,
      isRead,
      createdAt,
      updatedAt);
  }
  public interface ContentStep {
    IsReadStep content(String content);
  }
  

  public interface IsReadStep {
    CreatedAtStep isRead(Boolean isRead);
  }
  

  public interface CreatedAtStep {
    UpdatedAtStep createdAt(Temporal.DateTime createdAt);
  }
  

  public interface UpdatedAtStep {
    BuildStep updatedAt(Temporal.DateTime updatedAt);
  }
  

  public interface BuildStep {
    Message build();
    BuildStep id(String id);
    BuildStep sender(User sender);
    BuildStep receiver(User receiver);
  }
  

  public static class Builder implements ContentStep, IsReadStep, CreatedAtStep, UpdatedAtStep, BuildStep {
    private String id;
    private String content;
    private Boolean isRead;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private User sender;
    private User receiver;
    public Builder() {
      
    }
    
    private Builder(String id, User sender, User receiver, String content, Boolean isRead, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      this.id = id;
      this.sender = sender;
      this.receiver = receiver;
      this.content = content;
      this.isRead = isRead;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
    }
    
    @Override
     public Message build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Message(
          id,
          sender,
          receiver,
          content,
          isRead,
          createdAt,
          updatedAt);
    }
    
    @Override
     public IsReadStep content(String content) {
        Objects.requireNonNull(content);
        this.content = content;
        return this;
    }
    
    @Override
     public CreatedAtStep isRead(Boolean isRead) {
        Objects.requireNonNull(isRead);
        this.isRead = isRead;
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
     public BuildStep sender(User sender) {
        this.sender = sender;
        return this;
    }
    
    @Override
     public BuildStep receiver(User receiver) {
        this.receiver = receiver;
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
    private CopyOfBuilder(String id, User sender, User receiver, String content, Boolean isRead, Temporal.DateTime createdAt, Temporal.DateTime updatedAt) {
      super(id, sender, receiver, content, isRead, createdAt, updatedAt);
      Objects.requireNonNull(content);
      Objects.requireNonNull(isRead);
      Objects.requireNonNull(createdAt);
      Objects.requireNonNull(updatedAt);
    }
    
    @Override
     public CopyOfBuilder content(String content) {
      return (CopyOfBuilder) super.content(content);
    }
    
    @Override
     public CopyOfBuilder isRead(Boolean isRead) {
      return (CopyOfBuilder) super.isRead(isRead);
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
     public CopyOfBuilder sender(User sender) {
      return (CopyOfBuilder) super.sender(sender);
    }
    
    @Override
     public CopyOfBuilder receiver(User receiver) {
      return (CopyOfBuilder) super.receiver(receiver);
    }
  }
  

  public static class MessageIdentifier extends ModelIdentifier<Message> {
    private static final long serialVersionUID = 1L;
    public MessageIdentifier(String id) {
      super(id);
    }
  }
  
}
