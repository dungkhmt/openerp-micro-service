from translate import Translator

# Initialize the translator
translator = Translator(to_lang="en",from_lang="vi")

# Your text (can be Vietnamese or English)
text = "Chuyên viên cao cấp Phát triển ứng dụng Mobile"

# Translate the text to English
translation = translator.translate(text)

print(translation)
