# 安装包下载：
https://github.com/MellottStm/MusicPlayer/releases/tag/main
# 运行:
mvn clean javafx:run
# 项目打包:
1、mvn clean package
# 你可以选择:
# 控制台debug 打包
jpackage --name CoreMusic --icon "src\main\resources\Img\icon.ico" --input "target" --main-jar "MusicPlayer-1.0-SNAPSHOT-fat.jar" --main-class "com.smt.Main" --module-path "F:\openjfx-17.0.18_windows-x64_bin-jmods\javafx-jmods-17.0.18" --add-modules javafx.controls,java.logging,javafx.fxml,javafx.media,javafx.graphics,javafx.base,jdk.crypto.ec,java.sql --java-options "-Xmx2048m -Dfile.encoding=UTF-8 -Dhttps.protocols=TLSv1.2,TLSv1.3 -Djavax.net.debug=ssl:handshake" --type msi  --vendor "smt" --win-console --win-shortcut --win-menu --win-dir-chooser --win-per-user-install
# release打包
jpackage --name CoreMusic --icon "src\main\resources\Img\icon.ico" --input "target" --main-jar "MusicPlayer-1.0-SNAPSHOT-fat.jar" --main-class "com.smt.Main" --module-path "F:\openjfx-17.0.18_windows-x64_bin-jmods\javafx-jmods-17.0.18" --add-modules javafx.controls,java.logging,javafx.fxml,javafx.media,javafx.graphics,javafx.base,jdk.crypto.ec,java.sql --type msi  --vendor "smt" --win-shortcut --win-menu --win-dir-chooser --win-per-user-install
