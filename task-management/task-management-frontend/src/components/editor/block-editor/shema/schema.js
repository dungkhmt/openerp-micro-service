import {
  BlockNoteSchema,
  defaultBlockSpecs,
  defaultStyleSpecs,
  defaultInlineContentSpecs,
} from "@blocknote/core";
import { Banner } from "./block/banner";
import { Badge } from "./style/badge";
import { Mention } from "./inline-content/mention";
import { CheckList } from "./block/check-list";

export const schema = BlockNoteSchema.create({
  blockSpecs: {
    ...defaultBlockSpecs,
    banner: Banner,
    checklist: CheckList,
  },
  styleSpecs: {
    ...defaultStyleSpecs,
    badge: Badge,
  },
  inlineContentSpecs: {
    ...defaultInlineContentSpecs,
    mention: Mention,
  },
});
