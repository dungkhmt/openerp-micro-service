interface ICommonResponse<T> {
  code: string;
  message: string;
  result: T;
}

interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
}