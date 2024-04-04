import { Icon } from "@iconify/react";

function getPrevText(editor, { chars, offset = 0 }) {
  return editor._tiptapEditor.state.doc.textBetween(
    Math.max(0, editor._tiptapEditor.state.selection.from - chars),
    editor._tiptapEditor.state.selection.from - offset,
    "\n"
  );
}

/**
 * Completes a prompt using OpenAI service.
 *
 * @param {string} prompt - The prompt to be completed.
 * @param {BlockNoteEditor} editor - The editor instance.
 * @returns {Promise<void>} - A promise that resolves when the completion is done.
 */
async function complete(prompt, editor) {
  try {
    // const stream = await OpenAiService.getCompletion(prompt);
    // if (!stream) return;
    // const decoder = new TextDecoder("utf-8");
    // const reader = stream.getReader();
    // let { done, value } = await reader.read();
    // while (!done) {
    //   const chunk = decoder.decode(value);
    //   editor?._tiptapEditor.commands.insertContent(chunk);
    //   ({ done, value } = await reader.read());
    // }
  } catch (e) {
    console.error(e);
  }
}

/**
 * Inserts a magic AI block into the editor.
 *
 * @param {BlockNoteEditor} editor - The editor instance.
 * @returns {Object} - The magic AI block configuration.
 */
export const insertMagicAi = (editor) => {
  return {
    title: "Continue with AI",
    aliases: ["ai", "magic"],
    group: "Magic",
    icon: <Icon icon="bi:magic" fontSize={20} />,
    subtext: "Let AI finish your sentence",
    onItemClick: () => {
      const prompt = getPrevText(editor, { chars: 5000, offset: 1 });
      complete(prompt, editor);
    },
  };
};
