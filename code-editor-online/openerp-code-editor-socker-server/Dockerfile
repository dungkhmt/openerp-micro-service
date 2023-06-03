# syntax=docker/dockerfile:1

FROM node:14.19.3-alpine

# see section 5: https://snyk.io/blog/10-best-practices-to-containerize-nodejs-web-applications-with-docker/
RUN apk add dumb-init

# see section 3: https://snyk.io/blog/10-best-practices-to-containerize-nodejs-web-applications-with-docker/
ENV NODE_ENV=production

# This app will run on this port
ENV PORT 3001

# Create app directory
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY --chown=node:node package.json package-lock.json* ./

RUN npm ci --only=production

# Bundle app source
COPY --chown=node:node . .

EXPOSE ${PORT}

USER node

CMD ["dumb-init", "node", "app.js"]
