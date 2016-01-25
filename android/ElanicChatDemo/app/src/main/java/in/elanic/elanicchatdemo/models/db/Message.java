package in.elanic.elanicchatdemo.models.db;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MESSAGE".
 */
public class Message {

    private String message_id;
    private Integer type;
    private String content;
    private String receiver_id;
    private String sender_id;
    private java.util.Date created_at;
    private java.util.Date updated_at;
    private Boolean is_deleted;
    private Integer offer_price;
    private String product_id;
    private Integer offer_response;
    private java.util.Date delivered_at;
    private java.util.Date read_at;
    private java.util.Date offer_expiry;
    private Boolean is_read;
    private String seller_id;
    private String local_id;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MessageDao myDao;

    private User receiver;
    private String receiver__resolvedKey;

    private User sender;
    private String sender__resolvedKey;

    private Product product;
    private String product__resolvedKey;

    private User seller;
    private String seller__resolvedKey;


    public Message() {
    }

    public Message(String message_id) {
        this.message_id = message_id;
    }

    public Message(String message_id, Integer type, String content, String receiver_id, String sender_id, java.util.Date created_at, java.util.Date updated_at, Boolean is_deleted, Integer offer_price, String product_id, Integer offer_response, java.util.Date delivered_at, java.util.Date read_at, java.util.Date offer_expiry, Boolean is_read, String seller_id, String local_id) {
        this.message_id = message_id;
        this.type = type;
        this.content = content;
        this.receiver_id = receiver_id;
        this.sender_id = sender_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.is_deleted = is_deleted;
        this.offer_price = offer_price;
        this.product_id = product_id;
        this.offer_response = offer_response;
        this.delivered_at = delivered_at;
        this.read_at = read_at;
        this.offer_expiry = offer_expiry;
        this.is_read = is_read;
        this.seller_id = seller_id;
        this.local_id = local_id;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMessageDao() : null;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public java.util.Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(java.util.Date updated_at) {
        this.updated_at = updated_at;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Integer getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(Integer offer_price) {
        this.offer_price = offer_price;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Integer getOffer_response() {
        return offer_response;
    }

    public void setOffer_response(Integer offer_response) {
        this.offer_response = offer_response;
    }

    public java.util.Date getDelivered_at() {
        return delivered_at;
    }

    public void setDelivered_at(java.util.Date delivered_at) {
        this.delivered_at = delivered_at;
    }

    public java.util.Date getRead_at() {
        return read_at;
    }

    public void setRead_at(java.util.Date read_at) {
        this.read_at = read_at;
    }

    public java.util.Date getOffer_expiry() {
        return offer_expiry;
    }

    public void setOffer_expiry(java.util.Date offer_expiry) {
        this.offer_expiry = offer_expiry;
    }

    public Boolean getIs_read() {
        return is_read;
    }

    public void setIs_read(Boolean is_read) {
        this.is_read = is_read;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getLocal_id() {
        return local_id;
    }

    public void setLocal_id(String local_id) {
        this.local_id = local_id;
    }

    /** To-one relationship, resolved on first access. */
    public User getReceiver() {
        String __key = this.receiver_id;
        if (receiver__resolvedKey == null || receiver__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User receiverNew = targetDao.load(__key);
            synchronized (this) {
                receiver = receiverNew;
            	receiver__resolvedKey = __key;
            }
        }
        return receiver;
    }

    public void setReceiver(User receiver) {
        synchronized (this) {
            this.receiver = receiver;
            receiver_id = receiver == null ? null : receiver.getUser_id();
            receiver__resolvedKey = receiver_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public User getSender() {
        String __key = this.sender_id;
        if (sender__resolvedKey == null || sender__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User senderNew = targetDao.load(__key);
            synchronized (this) {
                sender = senderNew;
            	sender__resolvedKey = __key;
            }
        }
        return sender;
    }

    public void setSender(User sender) {
        synchronized (this) {
            this.sender = sender;
            sender_id = sender == null ? null : sender.getUser_id();
            sender__resolvedKey = sender_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Product getProduct() {
        String __key = this.product_id;
        if (product__resolvedKey == null || product__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductDao targetDao = daoSession.getProductDao();
            Product productNew = targetDao.load(__key);
            synchronized (this) {
                product = productNew;
            	product__resolvedKey = __key;
            }
        }
        return product;
    }

    public void setProduct(Product product) {
        synchronized (this) {
            this.product = product;
            product_id = product == null ? null : product.getProduct_id();
            product__resolvedKey = product_id;
        }
    }

    /** To-one relationship, resolved on first access. */
    public User getSeller() {
        String __key = this.seller_id;
        if (seller__resolvedKey == null || seller__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User sellerNew = targetDao.load(__key);
            synchronized (this) {
                seller = sellerNew;
            	seller__resolvedKey = __key;
            }
        }
        return seller;
    }

    public void setSeller(User seller) {
        synchronized (this) {
            this.seller = seller;
            seller_id = seller == null ? null : seller.getUser_id();
            seller__resolvedKey = seller_id;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
