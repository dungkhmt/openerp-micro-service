import { Helmet } from "react-helmet";
import { CircularProgressLoading } from "../../../components/common/loading/CircularProgressLoading";
import NotFound from "../../../views/errors/NotFound";
import Unknown from "../../../views/errors/Unknown";
import { useSelector } from "react-redux";
import { useAPIExceptionHandler } from "../../../hooks/useAPIExceptionHandler";
import {
  clearCache,
  clearErrors,
  fetchCreatedMeetingPlans,
  resetPagination,
  resetSort,
  setPagination,
  setSort,
  setSearch as setSearchAction,
} from "../../../store/created-meetings";
import { useDispatch } from "react-redux";
import { useCallback, useEffect, useState } from "react";
import { useDebounce } from "../../../hooks/useDebounce";
import CreatedMeetingsListPage from "../../../views/meetings/created/CreatedMeetingsListPage";

const statusCategories = {
  upcoming: [
    "PLAN_DRAFT",
    "PLAN_REG_OPEN",
    "PLAN_REG_CLOSED",
    "PLAN_ASSIGNED",
    "PLAN_IN_PROGRESS",
  ],
  over: ["PLAN_COMPLETED", "PLAN_CANCELED"],
};

const CreatedMeetings = () => {
  const dispatch = useDispatch();
  const {
    plansCache,
    pagination,
    sort,
    search: searchStore,
    fetchLoading,
    errors,
  } = useSelector((state) => state.createdMeetings);

  const [search, setSearch] = useState(searchStore || "");
  const [statusCategory, setStatusCategory] = useState("upcoming");
  const searchDebounce = useDebounce(search, 1000);
  const [isInitialized, setIsInitialized] = useState(false);

  // Build query string for search and status category
  const buildQueryString = useCallback(() => {
    const builder = [];
    // Handle search input
    const encodedSearch = encodeURIComponent(search).replace(/%20/g, "%1F");
    if (encodedSearch) {
      builder.push(
        encodedSearch
          ? `( name:*${encodedSearch}* OR description:*${encodedSearch}* OR location:*${encodedSearch}* )`
          : ""
      );
    }

    if (statusCategory) {
      const statuses = statusCategories[statusCategory];
      if (statuses && statuses.length > 0) {
        const statusQuery = statuses
          .map((status) => `statusId:${status}`)
          .join(" OR ");
        builder.push(`( ${statusQuery} )`);
      }
    }
    return builder.filter((s) => s !== "").join(" AND ");
  }, [search, statusCategory]);

  const getMeetingPlans = useCallback(async () => {
    if (plansCache[pagination.page]) return;
    try {
      await dispatch(
        fetchCreatedMeetingPlans({
          ...pagination,
          q: buildQueryString(),
          sort: `${sort.field},${sort.sort}`,
        })
      ).unwrap();
    } catch (error) {
      console.error("Error fetching meeting plans:", error);
    }
  }, [pagination, sort, buildQueryString, dispatch, plansCache]);

  const handlePaginationModel = (newModel) => {
    if (
      newModel.page === pagination.page &&
      newModel.pageSize !== pagination.size
    ) {
      dispatch(clearCache());
    }
    dispatch(setPagination({ page: newModel.page, size: newModel.pageSize }));
  };

  const handleSortModel = (newModel) => {
    if (newModel.length > 0) {
      dispatch(setSort({ field: newModel[0].field, sort: newModel[0].sort }));
    } else {
      dispatch(resetSort());
    }
  };

  const onSearch = () => {
    dispatch(resetPagination());
    dispatch(clearCache());
    dispatch(setSearchAction(searchDebounce));
  };

  const onStatusCategoryChange = (newCategory) => {
    setStatusCategory(newCategory);
    dispatch(resetPagination());
    dispatch(clearCache());
  };

  useEffect(() => {
    getMeetingPlans();
  }, [getMeetingPlans]);

  useEffect(() => {
    if (isInitialized) {
      onSearch();
    } else {
      setIsInitialized(true);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchDebounce]);

  const { errorType } = useAPIExceptionHandler(fetchLoading, errors, clearErrors);
  if (errorType === "notFound") return <NotFound />;
  if (errorType === "unknown") return <Unknown />;
  if (fetchLoading) return <CircularProgressLoading />;

  return (
    <>
      <Helmet>
        <title>Cuộc họp đã tạo | Task management</title>
      </Helmet>
      <CreatedMeetingsListPage
        search={search}
        setSearch={setSearch}
        onPaginationModelChange={handlePaginationModel}
        onSortModelChange={handleSortModel}
        statusCategory={statusCategory}
        onStatusCategoryChange={onStatusCategoryChange}
      />
    </>
  );
};

export default CreatedMeetings;
