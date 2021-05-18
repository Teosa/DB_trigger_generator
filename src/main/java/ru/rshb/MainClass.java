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
			String path = args[0];

			if (path == null || path.trim().length() == 0) {
				System.out.println("ОШИБКА. НЕОБХОДИМО УКАЗАТЬ ПУТЬ К ФАЙЛУ ИЛИ ПАПКЕ.");
				return;
			}

			if(path.contains(".txt")) {
				operateFile(path);
			} else {
				operateFolder(path);
			}
		} catch (CustomException e) {
			log.error(e.getMessage(), e);
			e.print();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.out.println(e.getMessage());
		}
	}

	private static void  operateFolder(String folderPath) {
		File folder = new File(folderPath);

		for(File file : folder.listFiles()) {
			if(file.getPath().contains(".txt")) {
				operateFile(file.getPath());
			}
		}
	}

	private static void  operateFile(String filePath) {
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
		List<TableColumnType> columns = parser.processLines(parser.parse());

		String output = ScriptGenerator.newBuilder()
				.setColumns(columns)
				.setOutputFolder(outputPath)
				.setTableName(tableName)
				.build()
				.generate();

		System.out.println("СГЕНЕРИРОВАН ФАЙЛ " + output);
	}

}
