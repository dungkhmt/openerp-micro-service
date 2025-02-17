import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { groupRepository } from 'repositories/groupRepository';

export const useGroupData = () => {
  const { data: groups, isLoading, error, refetch: refetchGroups } = useQuery(
    'groups',
    groupRepository.getAllGroups,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  const { data: allGroups, isLoading: isLoadingGroups, refetch: refetchAllGroups } = useQuery(
    "allGroups",
    groupRepository.getAllGroupsList,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000,
    }
  );

  const createMutation = useMutation(groupRepository.createGroup, {
    onSuccess: () => {
      refetchGroups();
      refetchAllGroups();
      toast.success('Tạo nhóm mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo nhóm');
    }
  });

  const updateMutation = useMutation(groupRepository.updateGroup, {
    onSuccess: () => {
      refetchGroups();
      refetchAllGroups();
      toast.success('Cập nhật nhóm thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật nhóm');
    }
  });

  const deleteMutation = useMutation(groupRepository.deleteGroup, {
    onSuccess: () => {
      refetchGroups();
      refetchAllGroups();
      toast.success('Xóa nhóm thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa nhóm');
    }
  });

  const updateGroupNameMutation = useMutation(groupRepository.updateGroupName, {
    onSuccess: () => {
      refetchGroups();
      refetchAllGroups();
      toast.success('Cập nhật tên nhóm thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi đổi tên nhóm');
    }
  });

  const deleteByIdMutation = useMutation(groupRepository.deleteById, {
    onSuccess: () => {
      refetchGroups();
      refetchAllGroups();
      toast.success('Xóa nhóm thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi xóa nhóm');
    }
  });

  return {
    groups: groups?.data || [],
    isLoading,
    error,
    createGroup: createMutation.mutateAsync,
    updateGroup: updateMutation.mutateAsync,
    deleteGroup: deleteMutation.mutateAsync,
    isCreating: createMutation.isLoading,
    isUpdating: updateMutation.isLoading,
    isDeleting: deleteMutation.isLoading,
    refetchGroups: refetchGroups,
    allGroups: allGroups?.data || [],
    isLoadingGroups,
    updateGroupName: updateGroupNameMutation.mutateAsync,
    isUpdatingName: updateGroupNameMutation.isLoading,
    deleteGroupById: deleteByIdMutation.mutateAsync,
    isDeletingById: deleteByIdMutation.isLoading,
  };
};
