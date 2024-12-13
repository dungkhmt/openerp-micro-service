import { useQuery, useMutation } from 'react-query';
import { toast } from 'react-toastify';
import { groupRepository } from 'repositories/groupRepository';

export const useGroupData = () => {
  const { data: groups, isLoading, error, refetch } = useQuery(
    'groups',
    groupRepository.getAllGroups,
    {
      staleTime: 5 * 60 * 1000,
      cacheTime: 30 * 60 * 1000
    }
  );

  const createMutation = useMutation(groupRepository.createGroup, {
    onSuccess: () => {
      refetch();
      toast.success('Tạo nhóm mới thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi tạo nhóm');
    }
  });

  const updateMutation = useMutation(groupRepository.updateGroup, {
    onSuccess: () => {
      refetch();
      toast.success('Cập nhật nhóm thành công!');
    },
    onError: (error) => {
      toast.error(error.response?.data || 'Có lỗi xảy ra khi cập nhật nhóm');
    }
  });

  const deleteMutation = useMutation(groupRepository.deleteGroup, {
    onSuccess: () => {
      refetch();
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
    refetchGroups: refetch,
  };
};
