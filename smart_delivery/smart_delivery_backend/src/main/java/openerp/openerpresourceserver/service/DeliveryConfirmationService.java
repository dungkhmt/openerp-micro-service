package openerp.openerpresourceserver.service;

import java.util.UUID;

/**
 * Service responsible for handling delivery confirmations
 */
public interface DeliveryConfirmationService {

    /**
     * Generate a confirmation code (OTP) for delivery confirmation
     */
    String generateConfirmationCode(UUID orderId);

    /**
     * Verify a confirmation code for delivery
     */
    boolean verifyConfirmationCode(UUID orderId, String code);

    /**
     * Store delivery confirmation details (signature, photo, etc.)
     */
    void storeDeliveryProof(UUID orderId, String proofType, String proofValue);

    /**
     * Confirm delivery with proof
     */
    boolean confirmDelivery(UUID orderId, String confirmationType, String confirmationValue);

    /**
     * Register a failed delivery attempt
     */
    void registerFailedDeliveryAttempt(UUID orderId, String reason);
}