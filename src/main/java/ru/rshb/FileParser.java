package ru.rshb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {

	private final File file;

	private final static Pattern columnNamePattern = Pattern.compile("(?<=[\\s\\t]|^)[a-zA-Z_]+(?=[\\s\\t])");

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

	public List<String> getColumnNames(List<String> lines) {
		List<String> columnNames = new ArrayList<>();

		try {
			for (String line : lines) {
				Matcher matcher = columnNamePattern.matcher(line);
				if(matcher.find()) {
					columnNames.add(matcher.group(0));
				} else {
					throw new CustomException("ОЩИБКА. НЕ УДАЛОСЬ ПОЛУЧИТЬ НАИМЕНОВАНИЕ КОЛОНКИ ИЗ СТРОКИ "+line+".");
				}
			}
		} catch (Exception e) {
			throw new CustomException("ОШИБКА ПОЛУЧЕНИЯ НАИМЕНОВАНИЯ КОЛОНКИ.", e);
		}

		return columnNames;
	}

}
