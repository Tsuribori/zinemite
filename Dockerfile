# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-17 AS build

WORKDIR /
COPY . /

RUN clj -Sforce -T:build all

FROM azul/zulu-openjdk-debian:17

ARG litestream_version="0.3.8"
ARG litestream_deb_filename="litestream-v${litestream_version}-linux-amd64.deb"

RUN set -x && \
    apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
      ca-certificates \
      wget \
      && \
    wget "https://github.com/benbjohnson/litestream/releases/download/v${litestream_version}/${litestream_deb_filename}" && \
    apt-get remove -y wget && \
    rm -rf /var/lib/apt/lists/*

RUN dpkg -i "${litestream_deb_filename}"

COPY --from=build docker_entrypoint.sh /zinemite/docker_entrypoint.sh
COPY --from=build litestream.yml /etc/litestream.yml
COPY --from=build /target/zinemite-standalone.jar /zinemite/zinemite-standalone.jar

RUN chmod +x /zinemite/docker_entrypoint.sh

EXPOSE $PORT

ENTRYPOINT ["/zinemite/docker_entrypoint.sh"]
