import { queryClient } from "queryClient";
import { useQuery, useMutation, useQueryClient } from "react-query";
import { toast } from "react-toastify";
import { groupRepository } from "repositories/groupRepository";

// Updated hook: now accepts a groupId to fetch group details by id
export const useGroupData = (groupId) => {
  const queryClient = useQueryClient();
  // Store the query key in a variable to use it consistently
  const queryKey = ["groupByGroupId", groupId];
  const allGroupsKey = "allGroups";

  const { data, isLoading, error } = useQuery(
    queryKey,
    () => groupRepository.getGroupByGroupId(groupId),
    {
      enabled: !!groupId,
      staleTime: 0, // Disable stale time to force refresh
      cacheTime: 0, // Disable cache to force refresh
    }
  );

  // Keep additional mutations (they remain unchanged)
  const { data: allGroups, isLoading: isLoadingGroups, refetch: refetchAllGroups } = useQuery(
    allGroupsKey,
    groupRepository.getAllGroupsList,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000,
    }
  );

  const createMutation = useMutation(groupRepository.createPiority, {
    onSuccess: () => {
      queryClient.refetchQueries(allGroupsKey);
      toast.success("Tạo nhóm mới thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi tạo nhóm");
    },
  });

  const createGroupMutation = useMutation(groupRepository.createGroup, {
    onSuccess: async () => {
      refetchAllGroups();
      toast.success("Tạo nhóm mới thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi tạo nhóm");
    },
  }
  );

  const updateMutation = useMutation(groupRepository.updateGroup, {
    onSuccess: async () => {
      if (groupId) {
        await queryClient.refetchQueries(queryKey);
      }
      toast.success("Cập nhật nhóm thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi cập nhật nhóm");
    },
  });

  const deleteMutation = useMutation(groupRepository.deleteGroup, {
    onSuccess: () => {
      queryClient.refetchQueries(queryKey);
      queryClient.refetchQueries(allGroupsKey);
      toast.success("Xóa nhóm thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi xóa nhóm");
    },
  });

  const updateGroupNameMutation = useMutation(groupRepository.updateGroupName, {
    onSuccess: () => {
      refetchAllGroups();
      toast.success("Cập nhật tên nhóm thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi đổi tên nhóm");
    },
  });

  const deleteByIdMutation = useMutation(groupRepository.deleteById, {
    onSuccess: () => {
      refetchAllGroups();
      toast.success("Xóa nhóm thành công!");
    },
    onError: (error) => {
      toast.error(error.response?.data || "Có lỗi xảy ra khi xóa nhóm");
    },
  });

  return {
    groups: data?.data || [], // Now using group details by groupId
    isLoading,
    error,
    createGroup: createMutation.mutateAsync,
    createGroupMutation: createGroupMutation.mutateAsync,
    updateGroup: updateMutation.mutateAsync,
    deleteGroup: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isCreatingGroup: createGroupMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading,
    refetchGroups: () => queryClient.refetchQueries(queryKey), // Replace refetch with queryClient.refetchQueries
    allGroups: allGroups?.data || [],
    isLoadingGroups,
    updateGroupName: updateGroupNameMutation.mutateAsync,
    isUpdatingName: updateGroupNameMutation.isLoading,
    deleteGroupById: deleteByIdMutation.mutateAsync,
    isDeletingById: deleteByIdMutation.isLoading,
  };
};
