import { useForm } from "react-hook-form";
import { format } from "date-fns";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form.tsx";
import { Input } from "@/components/ui/input.tsx";
import { Button } from "@/components/ui/button.tsx";
import { object, Output, string } from "valibot";
import { valibotResolver } from "@hookform/resolvers/valibot";

type RecordProps = {
  handleRecord: ({ date, time }: { date: string; time: string }) => void;
};

const Schema = object({
  date: string(),
  time: string(),
});

export function Record({ handleRecord }: RecordProps) {
  const date = new Date();

  const form = useForm<Output<typeof Schema>>({
    mode: "onSubmit",
    resolver: valibotResolver(Schema),
    defaultValues: {
      date: format(date, "yyyy-MM-dd"),
      time: date.toLocaleTimeString([], { hour12: false }),
    },
  });

  return (
    <div className="min-w-full">
      <h1 className="font-bold mb-5">Recording attendance</h1>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(handleRecord)}
          className="grid gap-y-3"
        >
          <FormField
            control={form.control}
            name="date"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Date</FormLabel>
                <FormControl>
                  <Input type="date" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="time"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Time</FormLabel>
                <FormControl>
                  <Input type="time" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <div>
            <Button type="submit">submit</Button>
          </div>
        </form>
      </Form>
    </div>
  );
}
