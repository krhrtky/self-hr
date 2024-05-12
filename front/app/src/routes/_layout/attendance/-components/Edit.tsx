import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Loader2, Pencil } from "lucide-react";
import {object, type Output, string} from "valibot";
import { useForm } from "react-hook-form";
import { valibotResolver } from "@hookform/resolvers/valibot";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form.tsx";
import {useState} from "react";

interface EditProps {
  readonly open?: boolean;
  readonly time: string;
  readonly id: string;
  readonly handleEdit: ({ time }: { time: string }) => Promise<void>;
  readonly inSubmitting: boolean;
}

const Schema = object({
  time: string(),
});

export function Edit({
  open = false,
  time,
  handleEdit,
  inSubmitting,
}: EditProps) {
  const form = useForm<Output<typeof Schema>>({
    mode: "onSubmit",
    resolver: valibotResolver(Schema),
    defaultValues: {
      time,
    },
  });
  const [isOpen, setIsOpen] = useState(open)
  return (
    <Dialog defaultOpen={isOpen} >
      <DialogTrigger asChild>
        <Button variant="ghost" size="icon">
          <Pencil size="1rem" />
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Edit attendance</DialogTitle>
          <DialogDescription>
            Fix attendance. Please input correct attendance.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form
            onSubmit={form.handleSubmit((values) => {
              handleEdit(values)
                  .then(() => setIsOpen(() => false));
            })}
            className="grid gap-y-3"
          >
            <FormField
              control={form.control}
              name="time"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Correct attendance</FormLabel>
                  <FormControl>
                    <Input type="time" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <DialogFooter>
              {inSubmitting ? (
                <Button type="button">
                  <Loader2 className="inline-block animate-spin" />
                </Button>
              ) : (
                <Button type="submit">submit</Button>
              )}
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
