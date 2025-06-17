FROM postgres:15.13-bookworm

# デフォルトの/var/lib/postgresql/data/postgresql.confだと/var/lib/postgresql/data/とかぶるため
COPY ./postgresql.conf /var/lib/postgresql/config/postgresql.conf

CMD [ "postgres", "-c", "config_file=/var/lib/postgresql/config/postgresql.conf" ]