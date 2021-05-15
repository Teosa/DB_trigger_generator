package ru.rshb;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class MainClass {

	private final static Pattern tableNamePattern = Pattern.compile("[a-zA-Z_]*(?=\\.txt)");

	private final static Pattern outputFolderPath = Pattern.compile(".*(?=(\\/|\\\\).*.txt)");

	private static final Logger log = Logger.getLogger(MainClass.class);

	public static void main(String[] args) {
		try {
			String filePath = args[0];

			if (filePath == null || filePath.trim().length() == 0) {
				System.out.println("ОШИБКА. НЕОБХОДИМО УКАЗАТЬ ПУТЬ К ФАЙЛУ.");
				return;
			}

			filePath = filePath.replace("\\", "/");

			Matcher outputFolderMatcher = outputFolderPath.matcher(filePath);
			Matcher tableNameMatcher = tableNamePattern.matcher(filePath);

			if (!outputFolderMatcher.find()) {
				System.out.println("ОШИБКА. НЕ УДАЛОСЬ ОПРЕДЕЛИТЬ ПУТЬ.");
				return;
			}

			if (!tableNameMatcher.find()) {
				System.out.println("ОШИБКА. НЕ УДАЛОСЬ ОПРЕДЕЛИТЬ НАЗВАНИЕ ТАБЛИЦЫ.");
				return;
			}

			String outputPath = outputFolderMatcher.group(0) + "/";
			String tableName = tableNameMatcher.group(0);

			File file = new File(filePath);

			if (!file.exists()) {
				System.out.println("ОШИБКА. НЕ УДАЛОСЬ НАЙТИ ФАЙЛ " + filePath + ".");
				return;
			}

			FileParser parser = new FileParser(file);
			List<String> lines = parser.parse();
			List<String> columnNames = parser.getColumnNames(lines);

			String output = ScriptGenerator.newBuilder()
					.setColumns(columnNames)
					.setColumnsWithTypes(lines)
					.setOutputFolder(outputPath)
					.setTableName(tableName)
					.build()
					.generate();

			System.out.println("СГЕНЕРИРОВАН ФАЙЛ " + output);
		} catch (CustomException e) {
			log.error(e.getMessage(), e);
			e.print();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.out.println(e.getMessage());
		}
	}

}
