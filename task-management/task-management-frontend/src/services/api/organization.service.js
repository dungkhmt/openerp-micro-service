import { isFunction } from "lodash";
import privateClient from "../client/private.client";

const endPoints = {
  getOrganizationsByMe: `/organizations`,
  getOrganizationById: (id) => `/organizations/${id}`,
  getLastOrganizationByMe: `/organizations/last`,
  createOrganization: `/organizations`,
  updateOrganization: (id) => `/organizations/${id}`,
  getUsersByOrganizationId: (id) => `/organizations/${id}/users`,
  removeUserFromOrganization: (id, userId) =>
    `/organizations/${id}/users/${userId}`,
  inviteUsers: `invitations`,
  getPendingInvitationsByMe: `/invitations/pending`,
  getPendingInvitationsByOrgId: (id) => `invitations/pending/org/${id}`,
  validateToken: (token) => `/invitations/validate?token=${token}`,
  acceptInvitation: (token) => `/invitations/accept?token=${token}`,
  declineInvitation: (token) => `/invitations/decline?token=${token}`,
};

const OrganizationService = {
  getOrganizationsByMe: async (cb) => {
    try {
      const response = await privateClient.get(endPoints.getOrganizationsByMe);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getOrganizationById: async (id, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getOrganizationById(id)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getLastOrganizationByMe: async (cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getLastOrganizationByMe
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  createOrganization: async (data, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.createOrganization,
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  updateOrganization: async (id, data, cb) => {
    try {
      const response = await privateClient.put(
        endPoints.updateOrganization(id),
        data
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getUsersByOrganizationId: async (id, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getUsersByOrganizationId(id)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  removeUserFromOrganization: async (id, userId, cb) => {
    try {
      const response = await privateClient.delete(
        endPoints.removeUserFromOrganization(id, userId)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getPendingInvitationsByOrgId: async (id, cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getPendingInvitationsByOrgId(id)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  inviteUsers: async (data, cb) => {
    try {
      const response = await privateClient.post(endPoints.inviteUsers, data);
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  getPendingInvitationsByMe: async (cb) => {
    try {
      const response = await privateClient.get(
        endPoints.getPendingInvitationsByMe
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  validateToken: async (token, cb) => {
    try {
      const response = await privateClient.get(endPoints.validateToken(token));
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  acceptInvitation: async (token, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.acceptInvitation(token)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
  declineInvitation: async (token, cb) => {
    try {
      const response = await privateClient.post(
        endPoints.declineInvitation(token)
      );
      if (response?.data && isFunction(cb)) cb(null, response.data);
      return response?.data;
    } catch (e) {
      if (isFunction(cb)) cb(e);
      else throw e;
    }
  },
};

export { OrganizationService };
