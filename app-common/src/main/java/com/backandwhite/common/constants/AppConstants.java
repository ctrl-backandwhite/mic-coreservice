package com.backandwhite.common.constants;

/**
 * Constantes compartidas para todos los microservicios.
 */
public final class AppConstants {

    private AppConstants() {
        // Clase de constantes, no se debe instanciar
    }

    // ========== Tópicos de Kafka — Catálogo ==========
    public static final String KAFKA_TOPIC_PRODUCT_CREATED = "catalog.product.created";
    public static final String KAFKA_TOPIC_PRODUCT_UPDATED = "catalog.product.updated";
    public static final String KAFKA_TOPIC_PRODUCT_DELETED = "catalog.product.deleted";
    public static final String KAFKA_TOPIC_PRODUCT_ENABLED = "catalog.product.enabled";
    public static final String KAFKA_TOPIC_PRODUCT_DISABLED = "catalog.product.disabled";
    public static final String KAFKA_TOPIC_CATEGORY_UPDATED = "catalog.category.updated";

    // ========== Tópicos de Kafka — Inventario ==========
    public static final String KAFKA_TOPIC_STOCK_RESERVED = "inventory.stock.reserved";
    public static final String KAFKA_TOPIC_STOCK_DEDUCTED = "inventory.stock.deducted";
    public static final String KAFKA_TOPIC_STOCK_RELEASED = "inventory.stock.released";
    public static final String KAFKA_TOPIC_STOCK_DEPLETED = "inventory.stock.depleted";
    public static final String KAFKA_TOPIC_STOCK_RESTORED = "inventory.stock.restored";
    public static final String KAFKA_TOPIC_STOCK_LOW_ALERT = "inventory.stock.low-alert";

    // ========== Tópicos de Kafka — Cliente ==========
    public static final String KAFKA_TOPIC_CUSTOMER_REGISTERED = "customer.registered";
    public static final String KAFKA_TOPIC_CUSTOMER_UPDATED = "customer.profile.updated";
    public static final String KAFKA_TOPIC_CUSTOMER_ADDRESS_ADDED = "customer.address.added";
    public static final String KAFKA_TOPIC_CUSTOMER_PASSWORD_RESET = "customer.password.reset-requested";
    public static final String KAFKA_TOPIC_CUSTOMER_NEWSLETTER_SUBSCRIBED = "customer.newsletter.subscribed";
    public static final String KAFKA_TOPIC_CUSTOMER_NEWSLETTER_UNSUBSCRIBED = "customer.newsletter.unsubscribed";

    // ========== Tópicos de Kafka — Carrito ==========
    public static final String KAFKA_TOPIC_CART_ITEM_ADDED = "cart.item.added";
    public static final String KAFKA_TOPIC_CART_ITEM_REMOVED = "cart.item.removed";
    public static final String KAFKA_TOPIC_CART_COUPON_APPLIED = "cart.coupon.applied";
    public static final String KAFKA_TOPIC_CART_CHECKOUT_INITIATED = "cart.checkout.initiated";
    public static final String KAFKA_TOPIC_CART_ABANDONED = "cart.abandoned";

    // ========== Tópicos de Kafka — Pedido ==========
    public static final String KAFKA_TOPIC_ORDER_CREATED = "order.created";
    public static final String KAFKA_TOPIC_ORDER_CONFIRMED = "order.confirmed";
    public static final String KAFKA_TOPIC_ORDER_STATUS_UPDATED = "order.status.updated";
    public static final String KAFKA_TOPIC_ORDER_CANCELLED = "order.cancelled";
    public static final String KAFKA_TOPIC_ORDER_SHIPPED = "order.shipped";
    public static final String KAFKA_TOPIC_ORDER_DELIVERED = "order.delivered";
    public static final String KAFKA_TOPIC_ORDER_RETURN_REQUESTED = "order.return.requested";
    public static final String KAFKA_TOPIC_ORDER_RETURN_APPROVED = "order.return.approved";
    public static final String KAFKA_TOPIC_ORDER_RETURNED = "order.returned";

    // ========== Tópicos de Kafka — Pago ==========
    public static final String KAFKA_TOPIC_PAYMENT_INITIATED = "payment.initiated";
    public static final String KAFKA_TOPIC_PAYMENT_CONFIRMED = "payment.confirmed";
    public static final String KAFKA_TOPIC_PAYMENT_FAILED = "payment.failed";
    public static final String KAFKA_TOPIC_PAYMENT_REFUND_INITIATED = "payment.refund.initiated";
    public static final String KAFKA_TOPIC_PAYMENT_REFUND_COMPLETED = "payment.refund.completed";

    // ========== Tópicos de Kafka — Envío ==========
    public static final String KAFKA_TOPIC_SHIPPING_ORDER_SHIPPED = "shipping.order.shipped";
    public static final String KAFKA_TOPIC_SHIPPING_ORDER_DELIVERED = "shipping.order.delivered";

    // ========== Tópicos de Kafka — Configuración ==========
    public static final String KAFKA_TOPIC_CONFIG_CURRENCY_RATE_UPDATED = "config.currency.rate-updated";
    public static final String KAFKA_TOPIC_CONFIG_LANGUAGE_ACTIVATED = "config.language.activated";

    // ========== Tópicos de Kafka — IAM ==========
    public static final String KAFKA_TOPIC_IAM_EMPLOYEE_CREATED = "iam.employee.created";
    public static final String KAFKA_TOPIC_IAM_EMPLOYEE_DISABLED = "iam.employee.disabled";
    public static final String KAFKA_TOPIC_IAM_AUTH_LOGIN_FAILED = "iam.auth.login-failed";

    // ========== Tópicos de Kafka — Impuestos ==========
    public static final String KAFKA_TOPIC_TAX_RULES_UPDATED = "tax.rules.updated";

    // ========== Tópicos de Kafka — Precio ==========
    public static final String KAFKA_TOPIC_PRICING_SPECIFIC_PRICE_CREATED = "pricing.specific-price.created";
    public static final String KAFKA_TOPIC_PRICING_PROMOTION_EXPIRED = "pricing.promotion.expired";

    // ========== Tópicos de Kafka — Notificación ==========
    public static final String KAFKA_TOPIC_NOTIFICATION_EMAIL = "notification.email.send";
    public static final String KAFKA_TOPIC_CMS_CONTACT_MESSAGE_RECEIVED = "cms.contact.message-received";

    // ========== Tópicos de Kafka — Media ==========
    public static final String KAFKA_TOPIC_MEDIA_IMAGE_UPLOADED = "media.image.uploaded";
    public static final String KAFKA_TOPIC_MEDIA_IMAGE_DELETED = "media.image.deleted";

    // ========== Grupos de Kafka ==========
    public static final String KAFKA_GROUP_NOTIFICATIONS = "notifications-group";
    public static final String KAFKA_GROUP_ANALYTICS = "analytics-group";
    public static final String KAFKA_GROUP_INVENTORY = "inventory-group";
    public static final String KAFKA_GROUP_ORDER = "order-group";
    public static final String KAFKA_GROUP_PAYMENT = "payment-group";
    public static final String KAFKA_GROUP_SHIPPING = "shipping-group";

    // ========== Caché ==========
    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_CUSTOMERS = "customers";
    public static final String CACHE_STOCK = "stock";
    public static final String CACHE_ORDERS = "orders";
    public static final String CACHE_PRICING = "pricing";
    public static final String CACHE_TAXES = "taxes";
    public static final String CACHE_SHIPPING = "shipping";
    public static final String CACHE_CONFIG = "config";

    // ========== Auditoría ==========
    public static final String AUDIT_CREATE = "CREATE";
    public static final String AUDIT_UPDATE = "UPDATE";
    public static final String AUDIT_DELETE = "DELETE";

    // ========== Roles ==========
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_BACKOFFICE = "BACKOFFICE";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_GUEST = "GUEST";

    // ========== Cabeceras HTTP propagadas por el Gateway ==========
    public static final String HEADER_AUTH_SUBJECT = "X-Auth-Subject";
    public static final String HEADER_AUTH_ROLES = "X-Auth-Roles";
    public static final String HEADER_AUTH_CUSTOMER_ID = "X-Auth-Customer-Id";
    public static final String HEADER_AUTH_EMPLOYEE_ID = "X-Auth-Employee-Id";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    // ========== Estados de Pedido ==========
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    public static final String ORDER_STATUS_RETURNED = "RETURNED";

    // ========== Estados de Pago ==========
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_AUTHORIZED = "AUTHORIZED";
    public static final String PAYMENT_STATUS_CAPTURED = "CAPTURED";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";

    // ========== Estados de Envío ==========
    public static final String SHIPMENT_STATUS_PENDING = "PENDING";
    public static final String SHIPMENT_STATUS_IN_TRANSIT = "IN_TRANSIT";
    public static final String SHIPMENT_STATUS_DELIVERED = "DELIVERED";
    public static final String SHIPMENT_STATUS_CANCELLED = "CANCELLED";
}
