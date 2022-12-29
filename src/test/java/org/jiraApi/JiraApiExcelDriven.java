package org.jiraApi;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import org.excelReader.XLS_Reader;
import org.testng.ITestResult;
import org.testng.annotations.*;
import java.io.IOException;
import static io.restassured.RestAssured.given;


public class JiraApiExcelDriven {
    XLS_Reader reader = new XLS_Reader("./api documentation.xlsx","JiraApi");
    SessionFilter sf = new SessionFilter();
    public JiraApiExcelDriven() throws IOException {

    }

    public String new_issue(int rowNumber) throws IOException {

        int expectedStatusCode = Integer.parseInt(reader.getCellData("Expected status Code",rowNumber));
        String body = reader.getCellData("Body",rowNumber);
        String endpoint = reader.getCellData("Endpoint",rowNumber);
        RestAssured.baseURI = reader.getCellData( "Base Uri",rowNumber);


        //login
        given().log().all().header("Content-Type","application/json").body("{ \"username\": \"dineshaher\", \"password\": \"123123666\" }").
                filter(sf).when().post("/rest/auth/1/session").
                then().log().all().assertThat().statusCode(200);

        //creating issue

        return given().log().all().header("Content-Type","application/json")
                .body(body).filter(sf).
                when().post(endpoint).
                then().log().all().assertThat().statusCode(expectedStatusCode).log().all().extract().response().asString();

    }
    @Test
    public void test_1() throws IOException {
        String response =  new_issue(2);

    }


    @AfterMethod
    public void afterMethod(ITestResult result)
    {
        try
        {
            if(result.getStatus() == ITestResult.SUCCESS)
            {

                reader.setCellData( "Remark",2,"Passed");
                System.out.println("passed **********");
            }

            else if(result.getStatus() == ITestResult.FAILURE)
            {
                reader.setCellData( "Remark",2,"Failed");
                System.out.println("Failed ***********");

            }

            else if(result.getStatus() == ITestResult.SKIP ){
                reader.setCellData( "Remark",2,"Skipped");


                System.out.println("Skiped***********");

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}

