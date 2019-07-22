package com.example.enpinterface.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.example.enpinterface.model.Employee;

 
@Configuration
@EnableBatchProcessing
public class BatchConfig
{
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
     
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    ItemWriter<Employee> writer;
    
    @Autowired
    public DataSource dataSource;
 
    @Value("input/inputData*.csv")
    private Resource[] inputResources;
     
    private Resource outputResource = new FileSystemResource("output/outputData.csv");
    
    private Resource outputResource1 = new FileSystemResource("output/outputData.txt");
    
    private Resource outputResource2 = new FileSystemResource("output/outputData.json");
 
    /*@Bean
    public FlatFileItemWriter<Employee> writer()
    {
        //Create writer instance
        FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(outputResource);
         
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);
         
        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<Employee>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Employee>() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName" });
                    }
                });
            }
        });
        return writer;
    }*/
    
    /*@Bean
	public FlatFileItemWriter writer() {
	        return  new FlatFileItemWriterBuilder<Employee>()
	                                   .name("itemWriter")
	                                   .resource(outputResource1)
	                                   .lineAggregator(new PassThroughLineAggregator<>())
	                                   .build();
	}*/
    
    @Bean
    public JsonFileItemWriter<Employee> writer() {
       return new JsonFileItemWriterBuilder<Employee>()
                     .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                     .resource(outputResource2)
                     .name("tradeJsonFileItemWriter")
                     .build();
    }
 
    @Bean
    public Job readCSVFilesJob() {
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }
 
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1").<Employee, Employee>chunk(5)
                .reader(reader())
                .writer(writer)
                .build();
    }
 
    /*@Bean
    public MultiResourceItemReader<Employee> multiResourceItemReader()
    {
        MultiResourceItemReader<Employee> resourceItemReader = new MultiResourceItemReader<Employee>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate(reader());
        return resourceItemReader;
    }*/
 
   /* @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    public FlatFileItemReader<Employee> reader()
    {
        //Create reader instance
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
         
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);  
         
        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName" });
                    }
                });
                //Set values in Employee class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {
                    {
                        setTargetType(Employee.class);
                    }
                });
            }
        });
        return reader;
    }*/
    
    @Bean
    public DataSource dataSource() {
     final DriverManagerDataSource dataSource = new DriverManagerDataSource();
     dataSource.setDriverClassName("com.mysql.jdbc.Driver");
     dataSource.setUrl("jdbc:mysql://localhost/springbatch");
     dataSource.setUsername("root");
     dataSource.setPassword("root");
     
     return dataSource;
    }
    
    @Bean
    public JdbcCursorItemReader<Employee> reader(){
     JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<Employee>();
     reader.setDataSource(dataSource);
     reader.setSql("SELECT id,name FROM user");
     reader.setRowMapper(new UserRowMapper());
     
     return reader;
    }
    
    public class UserRowMapper implements RowMapper<Employee>{

     @Override
     public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
    	 
      Employee user = new Employee();
      user.setId(rs.getString("id"));
      user.setFirstName(rs.getString("name"));
      
      return user;
     }
     
    }
}