FROM gradle:ubi-minimal

# 作業ディレクトリの設定
WORKDIR /app

# 必要なファイルのコピー
#

# デフォルトポートの公開
EXPOSE 8080

CMD [ "sleep", "infinity" ]


# # 実行用のスクリプトをコピー
# COPY ./entrypoint.sh .
# RUN \
#     # 改行コードを揃えて、
#     sed -i -e 's/\r//g' ./entrypoint.sh && \
#     # 権限も変更
#     chmod +x ./entrypoint.sh

# # スクリプトで起動
# ENTRYPOINT ["/bin/bash", "-c", "./entrypoint.sh"]
