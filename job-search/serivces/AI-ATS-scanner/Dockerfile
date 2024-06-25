# Use the official Python image from the Docker Hub
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

# Copy the requirements.txt file into the container
COPY requirements.txt .

# Install the dependencies
RUN pip install --no-cache-dir -r requirements.txt
RUN apt-get install -y poppler-utils

# Copy the rest of the application code into the container
COPY . .

# Create the .streamlit directory and add the config.toml file to disable CORS
RUN mkdir -p ~/.streamlit && \
    echo "\
    [server]\n\
    enableCORS = false\n\
    enableXsrfProtection = false\n\
    " > ~/.streamlit/config.toml

# Expose the port that Streamlit runs on
EXPOSE 8501

# Command to run the Streamlit app
CMD ["streamlit", "run", "your_app.py", "--server.port=8501", "--server.address=0.0.0.0"]
