package ru.rshb;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptGenerator {

	private String outputFolder;

	private String outputFileName;

	private String tableName;

	private List<String> columnsWithTypes;

	private List<String> columns;

	private final static String TEMPLATE_NAME = "script_pattern.ftl";

	private final Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_31);

	public String generate() {
		createFile();

		try (Writer out = new OutputStreamWriter(new FileOutputStream(outputFileName))) {

			configFreeMarker();

			Template freeMarkerTemplate = freeMarkerConfig.getTemplate(TEMPLATE_NAME);
			freeMarkerTemplate.process(createModel(), out);

		} catch (IOException | TemplateException e) {
			throw new CustomException("ОШИБКА СОЗДАНИЯ СКРИПТА.", e);
		}

		return outputFileName;
	}

	private Map<String, Object> createModel() {
		Map<String, Object> model = new HashMap<>();

		model.put("tableName", tableName);
		model.put("columnsWithTypes", columnsWithTypes);
		model.put("columns", columns);

		return model;
	}

	private void createFile() {
		try {
			System.out.println(outputFileName);
			new File(outputFileName).createNewFile();
		} catch (IOException e) {
			throw new CustomException("ОШИБКА СОЗДАНИЯ ФАЙЛА.", e);
		}
	}

	private void configFreeMarker() throws IOException {
		String resourceFolderPath =
				new File(ScriptGenerator.class.getClassLoader().getResource(TEMPLATE_NAME).getFile()).getParent();
		freeMarkerConfig.setDirectoryForTemplateLoading(new File(resourceFolderPath));
		freeMarkerConfig.setDefaultEncoding("UTF-8");
	}

	public static Builder newBuilder() {
		return new ScriptGenerator().new Builder();
	}

	public class Builder {

		private Builder() {
			// private constructor
		}

		public Builder setOutputFolder(String outputFolder) {
			ScriptGenerator.this.outputFolder = outputFolder;
			return this;
		}

		public Builder setTableName(String tableName) {
			ScriptGenerator.this.tableName = tableName;
			return this;
		}

		public Builder setColumnsWithTypes(List<String> columnsWithTypes) {
			ScriptGenerator.this.columnsWithTypes = columnsWithTypes;
			return this;
		}

		public Builder setColumns(List<String> columns) {
			ScriptGenerator.this.columns = columns;
			return this;
		}

		public ScriptGenerator build() {
			ScriptGenerator.this.outputFileName = ScriptGenerator.this.outputFolder + ScriptGenerator.this.tableName + ".sql";
			return ScriptGenerator.this;
		}

	}

}
