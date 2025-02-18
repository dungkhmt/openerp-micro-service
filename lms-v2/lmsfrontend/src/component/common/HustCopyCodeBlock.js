import {Typography} from "@mui/material";
import React from "react";
import {CopyBlock, dracula} from "react-code-blocks";

const HustCopyBlock = (props) => {
  const {
    text,
    showLineNumbers = false,
    wrapLines = true,
    title,
    ...remainProps
  } = props;

  return (
    <>
      {title && (
        <Typography variant="h6" sx={{mb: 1}}>
          {title}
        </Typography>
      )}
      <CopyBlock
        codeBlock
        text={text || " "}
        showLineNumbers={showLineNumbers}
        theme={dracula}
        wrapLines={wrapLines}
        {...remainProps}
        customStyle={{
          fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
          fontVariantLigatures: "none",
        }}
      />
    </>
  );
};

export default React.memo(HustCopyBlock);
