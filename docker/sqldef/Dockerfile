FROM debian:bookworm

RUN apt update \
    && apt install -y wget \
    && wget https://github.com/k0kubun/sqldef/releases/download/v0.16.15/psqldef_linux_amd64.tar.gz -O sqldef.tar.gz \
    && tar -zxvf sqldef.tar.gz -C /bin \
    && rm sqldef.tar.gz \
    && apt clean
