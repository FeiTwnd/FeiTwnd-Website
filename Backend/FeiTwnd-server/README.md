# FeiTwnd Backend 配置说明

## 配置文件设置

本项目使用模板文件来保护敏感配置信息。

### 初次使用步骤

1. 复制模板文件：
```bash
cp src/main/resources/application.yml.template src/main/resources/application.yml
```

2. 编辑 `application.yml`，填写实际的配置信息：
   - `feitwnd.jwt.secret-key`: 你的 JWT 密钥
   - `feitwnd.alioss.access-key-id`: 阿里云 OSS Access Key ID
   - `feitwnd.alioss.access-key-secret`: 阿里云 OSS Access Key Secret
   - `feitwnd.alioss.bucket-name`: 你的 OSS Bucket 名称

3. 确保你还需要配置环境变量或在 `application-dev.yml` 中配置：
   - `feitwnd.datasource.driver-class-name`
   - `feitwnd.datasource.host`
   - `feitwnd.datasource.port`
   - `feitwnd.datasource.database`
   - `feitwnd.datasource.username`
   - `feitwnd.datasource.password`
   - `feitwnd.redis.host`
   - `feitwnd.redis.port`
   - `feitwnd.redis.password`
   - `feitwnd.redis.database`

### 注意事项

- **切勿** 将 `application.yml` 文件提交到 Git 仓库
- 只提交 `application.yml.template` 模板文件
- 实际的配置文件已在 `.gitignore` 中被忽略
