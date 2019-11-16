using System;
using System.Data;
using System.Data.SqlClient;
using Dapper;

namespace netcore
{
    class Program
    {
        const string ConnectionString = @"Data Source=<servername>\SQLEXPRESS;Initial Catalog=SampleDB;User ID=<user>;Password=<password>;Connect Timeout=30;Encrypt=False;";
        static void Main(string[] args)
        {
            標準実装();
            UseDapper();
            UseDapperWithResultClass();
        }

        static void 標準実装()
        {
            System.Console.WriteLine(nameof(標準実装));
            using (var conn = new SqlConnection(ConnectionString))
            using (var cmd = new SqlCommand(@"select [受注日], [金額] from [受注] where [受注日] < @p1", conn))
            {
                conn.Open();
                cmd.Parameters.Add(new SqlParameter("@p1", SqlDbType.Date));
                cmd.Parameters[0].Value = new DateTime(2019, 9, 1);

                var reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    System.Console.WriteLine($"受注日:{reader.GetDateTime(0)} 金額:{reader.GetInt32(1)}");
                }
            }
        }

        static void UseDapper()
        {
            System.Console.WriteLine(nameof(UseDapper));
            using (var conn = new SqlConnection(ConnectionString))
            {
                var results = conn.Query(@"select [受注日], [金額] from [受注] where [受注日] < @p1", 
                    new { p1 = new DateTime(2019, 9, 1) });
                foreach (var r in results)
                {
                    System.Console.WriteLine($"受注日:{r.受注日} 金額:{r.金額}");
                }
            }
        }

        static void UseDapperWithResultClass()
        {
            System.Console.WriteLine(nameof(UseDapperWithResultClass));
            using (var conn = new SqlConnection(ConnectionString))
            {
                var results = conn.Query<受注>(@"select [受注日], [金額] from [受注] where [受注日] < @p1", 
                    new { p1 = new DateTime(2019, 9, 1) });
                foreach (var r in results)
                {
                    System.Console.WriteLine($"受注日:{r.受注日} 金額:{r.金額}");
                }
            }
        }
    }
    class 受注
    {
        public DateTime 受注日 { get; set; }
        public int 金額 { get; set; }
    }
}
