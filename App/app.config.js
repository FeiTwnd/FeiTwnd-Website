// EAS 项目标识（projectId / owner）从本地 gitignored 的 .local.eas.json 读取，
// 避免把个人账号信息提交进仓库。构建前请创建该文件（参考 .local.eas.example.json）。
//
// 说明：
// - 本地 `eas build` / `eas env:set` 求值本文件时会读到 projectId，从而识别你的 EAS 项目。
// - 云端构建的归档不包含 gitignored 文件，求值时 projectId 为空，但构建任务上下文已由 CLI 传递，不影响打包。
// - 文件缺失或损坏时按"未配置"处理，仅影响 EAS 关联，不影响本地开发（expo start / expo export）。
const fs = require('fs');
const path = require('path');
const appJson = require('./app.json');

let local = {};
const localFile = path.join(__dirname, '.local.eas.json');
try {
  if (fs.existsSync(localFile)) {
    local = JSON.parse(fs.readFileSync(localFile, 'utf8'));
  }
} catch {
  // 文件损坏按缺失处理
}

const expo = { ...appJson.expo };

if (local.projectId) {
  expo.extra = { ...expo.extra, eas: { projectId: local.projectId } };
}
if (local.owner) {
  expo.owner = local.owner;
}

module.exports = expo;
