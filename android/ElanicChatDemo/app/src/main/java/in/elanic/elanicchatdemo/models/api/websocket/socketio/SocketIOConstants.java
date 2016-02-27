package in.elanic.elanicchatdemo.models.api.websocket.socketio;

/**
 * Created by Jay Rambhia on 2/23/16.
 */
public class SocketIOConstants {

    public static final String EVENT_JOIN_CHAT = "joinChat";
    public static final String EVENT_ADD_USER = "addUser";
    public static final String EVENT_SEND_CHAT = "sendChat";
    public static final String EVENT_MAKE_OFFER = "makeOffer";
    public static final String EVENT_EDIT_OFFER_STATUS = "editOfferStatus";
//    public static final String EVENT_ACCEPT_OFFER = "acceptOffer";
//    public static final String EVENT_DENY_OFFER = "denyOffer";
//    public static final String EVENT_CANCEL_OFFER = "cancelOffer";
    public static final String EVENT_SET_QUOTATIONS_DELIVERED_ON = "setQuotationDeliveredOn";
    public static final String EVENT_SET_MESSAGES_DELIVERD_ON = "setChatDeliveredOn";
    public static final String EVENT_SET_QUOTATIONS_READ_AT = "setQuotationReadAt";
    public static final String EVENT_SET_MESSAGES_READ_AT = "setChatReadAt";
    public static final String EVENT_BUY_NOW = "buyNow";
    public static final String EVENT_LEAVE_ROOM = "leave_room";
    public static final String EVENT_DISCONNECT = "disconnect";

    @Deprecated public static final String EVENT_GET_MESSAGES = "getMessages";
    @Deprecated public static final String EVENT_GET_QUOTATIONS = "getQuotations";

    public static final String EVENT_CONFIRM_JOIN_CHAT = "confirmJoinChat";
    public static final String EVENT_CONFIRM_ADD_USER = "confirmAddUser";
    public static final String EVENT_CONFIRM_SEND_CHAT = "confirmSendChat";
//    public static final String EVENT_REVOKE_SEND_CHAT = "revokeSendChat";
    public static final String EVENT_CONFIRM_MAKE_OFFER = "confirmMakeOffer";
    public static final String EVENT_CONFIRM_EDIT_OFFER_STATUS = "confirmEditOfferStatus";
//    public static final String EVENT_REVOKE_MAKE_OFFER = "revokeMakeOffer";

    @Deprecated public static final String EVENT_CONFIRM_ACCEPT_OFFER = "confirmAcceptOffer";
//    public static final String EVENT_REVOKE_ACCEPT_OFFER = "revokeAcceptOffer";
    @Deprecated public static final String EVENT_CONFIRM_DENY_OFFER = "confirmDenyOffer";
//    public static final String EVENT_REVOKE_DENY_OFFER = "revokeDenyOffer";
    public static final String EVENT_CONFIRM_BUY_NOW = "confirmBuyNow";
//    public static final String EVENT_REVOKE_BUY_NOW = "revokeBuyNow";
    public static final String EVENT_CONFIRM_CANCEL_OFFER = "confirmCancelOffer";
//    public static final String EVENT_REVOKE_CANCEL_OFFER = "denyCancelOffer";
    public static final String EVENT_CONFIRM_SET_QUOTATIONS_DELIVERED_ON = "confirmSetQuotationDeliveredOn";
    public static final String EVENT_CONFIRM_SET_MESSAGES_DELIVERED_ON = "confirmSetChatDeliveredOn";
    public static final String EVENT_CONFIRM_SET_QUOTATIONS_READ_AT = "confirmSetQuotationReadAt";
    public static final String EVENT_CONFIRM_SET_MESSAGES_READ_AT = "confirmSetChatReadAt";
//    public static final String EVENT_REVOKE_SET_QUOTATIONS_DELIVERED_ON = "revokeSetQuotationsDeliveredOn";
//    public static final String EVENT_REVOKE_SET_MESSAGES_DELIVERED_ON = "revokeSetMessagesDeliveredOn";
//    public static final String EVENT_REVOKE_SET_QUOTATIONS_READ_AT = "revokeSetQuotationsReadAt";
//    public static final String EVENT_REVOKE_SET_MESSAGES_READ_AT = "revokeSetMessagesReadAt";

}
