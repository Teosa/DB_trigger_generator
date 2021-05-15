package ru.rshb;


import freemarker.cache.TemplateLoader;
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

	private String outputFile;

	private String tableName;

	private List<String> columnsWithTypes;

	private List<String> columns;

	private final static String TEMPLATE_NAME = "script_pattern.ftl";

	private final Configuration freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_31);

	public void generate() {
		createFile();

		try (Writer out = new OutputStreamWriter(new FileOutputStream(outputFile))) {

			configFreeMarker();

			Template freeMarkerTemplate = freeMarkerConfig.getTemplate(TEMPLATE_NAME);
			freeMarkerTemplate.process(createModel(), out);

		} catch (IOException | TemplateException e) {
			throw new CustomException("ОШИБКА СОЗДАНИЯ СКРИПТА.", e);
		}
	}

	private Map<String, Object> createModel() {
		Map<String, Object> model = new HashMap<>();

		model.put("tableName", tableName);
		model.put("columnsWithTypes", columnsWithTypes);
		model.put("columns", columns);

		return model;
	}

	private void createFile() {
		try{
			System.out.println(outputFile);
			new File(outputFile).createNewFile();
		} catch (IOException e) {
			throw new CustomException("ОШИБКА СОЗДАНИЯ ФАЙЛА.", e);
		}
	}

	private void configFreeMarker() throws IOException {
		String r = new File(ScriptGenerator.class.getClassLoader().getResource(TEMPLATE_NAME).getFile()).getParent();
		System.out.println(r);
	freeMarkerConfig.setDirectoryForTemplateLoading(new File(r));
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
			ScriptGenerator.this.outputFile = ScriptGenerator.this.outputFolder + ScriptGenerator.this.tableName + ".sql";
			return ScriptGenerator.this;
		}

	}

}
