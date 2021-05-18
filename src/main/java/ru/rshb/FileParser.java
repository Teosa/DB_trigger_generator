package ru.rshb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {

	private final File file;

	private final static String columnNameRegex = "^[\\t\\s]*[a-zA-Z_]*";

	private final static Pattern columnNamePattern = Pattern.compile(columnNameRegex);

	public FileParser(File file) {
		this.file = file;
	}

	public List<String> parse() {
		try {
			return Files.readAllLines(file.toPath());
		} catch (IOException e) {
			throw new CustomException("ОШИБКА ПАРСИНГА.", e);
		}
	}

	public List<TableColumnType> processLines(List<String> lines) {
		List<TableColumnType> processedLines = new ArrayList<>();

		for (String line : lines) {
			line = line.replaceAll(",$", "");
			line = line.replace("NOT NULL", "");
			line = line.replace("CHARACTER VARYING", "VARCHAR");
			line = line.replace("DEFAULT false", "");
			line = line.replace("DEFAULT true", "");

			TableColumnType column = new TableColumnType();
			column.setName(getColumnName(line));
			column.setType(getColumnType(line));

			processedLines.add(column);
		}

		return processedLines;
	}

	public String getColumnName(String line) {
		try {
			Matcher matcher = columnNamePattern.matcher(line);
			if (matcher.find()) {
				return matcher.group(0).trim().toLowerCase(Locale.ROOT);
			} else {
				throw new CustomException("ОШИБКА. НЕ УДАЛОСЬ ПОЛУЧИТЬ НАИМЕНОВАНИЕ КОЛОНКИ ИЗ СТРОКИ " + line + ".");
			}
		} catch (Exception e) {
			throw new CustomException("ОШИБКА ПОЛУЧЕНИЯ НАИМЕНОВАНИЯ КОЛОНКИ.", e);
		}
	}

	public String getColumnType(String line) {
		try {
			return line.replaceAll(columnNameRegex, "").trim();
		} catch (Exception e) {
			throw new CustomException("ОШИБКА ПОЛУЧЕНИЯ ТИПА КОЛОНКИ.", e);
		}
	}

}
