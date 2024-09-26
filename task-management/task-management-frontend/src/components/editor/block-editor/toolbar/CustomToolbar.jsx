import {
  BasicTextStyleButton,
  BlockTypeSelect,
  ColorStyleButton,
  CreateLinkButton,
  FormattingToolbar,
  ImageCaptionButton,
  NestBlockButton,
  ReplaceImageButton,
  TextAlignButton,
  UnnestBlockButton,
} from "@blocknote/react";
import { BadgeButton } from "./BadgeButton";

const CustomToolbar = () => {
  return (
    <FormattingToolbar>
      <BlockTypeSelect key={"blockTypeSelect"} />

      <ImageCaptionButton key={"imageCaptionButton"} />
      <ReplaceImageButton key={"replaceImageButton"} />

      <BasicTextStyleButton basicTextStyle={"bold"} key={"boldStyleButton"} />
      <BasicTextStyleButton
        basicTextStyle={"italic"}
        key={"italicStyleButton"}
      />
      <BasicTextStyleButton
        basicTextStyle={"underline"}
        key={"underlineStyleButton"}
      />
      <BasicTextStyleButton
        basicTextStyle={"strike"}
        key={"strikeStyleButton"}
      />
      {/* Extra button to toggle code styles */}
      <BasicTextStyleButton key={"codeStyleButton"} basicTextStyle={"code"} />

      <BadgeButton />

      <TextAlignButton textAlignment={"left"} key={"textAlignLeftButton"} />
      <TextAlignButton textAlignment={"center"} key={"textAlignCenterButton"} />
      <TextAlignButton textAlignment={"right"} key={"textAlignRightButton"} />

      <ColorStyleButton key={"colorStyleButton"} />

      <NestBlockButton key={"nestBlockButton"} />
      <UnnestBlockButton key={"unnestBlockButton"} />

      <CreateLinkButton key={"createLinkButton"} />
    </FormattingToolbar>
  );
};

export { CustomToolbar };
