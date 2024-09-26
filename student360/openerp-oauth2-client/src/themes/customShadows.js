import { alpha } from '@mui/material/styles';
import palette from './palette';

const color = palette.common.black;

export default function customShadows() {
  return {
    dropdown: `0px 32px 48px -8px ${alpha(color, 0.1)}, 0px 0px 14px -4px ${alpha(color, 0.05)}`,
  };
}
